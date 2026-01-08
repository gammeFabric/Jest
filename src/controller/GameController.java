package controller;

import app.SaveManager;
import app.GameConfigurationManager;
import model.cards.ExtensionCard;
import model.game.ExtensionManager;
import model.game.GameVariant;
import model.game.GameConfiguration;
import model.game.variants.StandardVariant;
import model.game.variants.FullHandVariant;
import model.game.variants.ReverseScoringVariant;
import model.game.FullHandRound;
import model.players.Player;
import model.players.strategies.StrategyType;
import model.game.Round;
import view.interfaces.IGameView;
import model.game.Game;
import view.interfaces.IRoundView;
import view.ViewFactory;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final Game model;
    private final IGameView gameView;
    private final IRoundView roundView;
    private final ViewFactory viewFactory;
    private GameConfiguration gameConfiguration;
    private ArrayList<ExtensionCard> selectedExtensions;

    public GameController(Game model, IGameView gameView, IRoundView roundView, ViewFactory viewFactory) {
        this.model = model;
        this.gameView = gameView;
        this.roundView = roundView;
        this.viewFactory = viewFactory;
    }

    public void addPlayers(){
        int playerCount = gameView.askNumberOfPlayers();
        for (int i = 1; i <= playerCount; i++) {
            String name = gameView.askPlayerName(i);
            boolean isHuman = gameView.isHumanPlayer(name);
            if (isHuman) {
                model.addHumanPlayer(name);
            } else {
                StrategyType strategy = gameView.askStrategy(name);
                model.addVirtualPlayer(name, strategy);
            }
        }
        gameView.showPlayers(model.getPlayers());
    }

    public void startGame(){
        if (model.getPlayers() == null || model.getPlayers().isEmpty()) {
            // Select variant before adding players (variant may affect player limits)
            selectVariant();

            addPlayers();

            // Validation: Use variant's player limits
            GameVariant variant = model.getVariant();
            int playerCount = model.getPlayers().size();
            if (playerCount < variant.getMinPlayers() || playerCount > variant.getMaxPlayers()) {
                throw new IllegalStateException(
                    "This variant supports " + variant.getMinPlayers() + 
                    " to " + variant.getMaxPlayers() + " players only."
                );
            }

            // Handle extensions before choosing trophies
            handleExtensions();

            model.chooseTrophies(model.getPlayers().size());
            gameView.showTrophies(model.trophiesInfo());
            
            // Save the game configuration for restart functionality
            saveGameConfiguration();
        }

        playGame();
    }

    /**
     * Selects a game variant and sets it on the Game model.
     */
    private void selectVariant() {
        // Get available variants
        List<GameVariant> availableVariants = getAvailableVariants();
        
        // Ask view to select a variant
        GameVariant selectedVariant = gameView.askForVariant(availableVariants);
        
        // Set the variant on the game model
        model.setVariant(selectedVariant);
    }

    /**
     * Returns the list of available game variants.
     * @return list of available variants
     */
    private List<GameVariant> getAvailableVariants() {
        List<GameVariant> variants = new ArrayList<>();
        variants.add(new StandardVariant());
        variants.add(new ReverseScoringVariant());
        variants.add(new FullHandVariant());
        return variants;
    }

    /**
     * Handles the logic for proposing and adding extensions.
     * Separated into its own method for clarity.
     */
    private void handleExtensions() {
        ArrayList<ExtensionCard> availableExtensions = ExtensionManager.getAvailableExtensions();
        boolean validSelection = false;

        while (!validSelection) {
            // 1. Ask the view for user selection
            ArrayList<Integer> selectedIndices = gameView.askForExtensions(availableExtensions);

            // 2. Validate the selection
            int playerCount = model.getPlayers().size();

            if (ExtensionManager.isValidSelection(selectedIndices, playerCount)) {
                validSelection = true;

                // Track selected extensions for configuration
                selectedExtensions = new ArrayList<>();

                // 3. Add selected extensions to the deck
                if (!selectedIndices.isEmpty()) {
                    ArrayList<ExtensionCard> cardsToAdd = new ArrayList<>();
                    System.out.println("Validation OK. Adding cards to the deck:");

                    for (int index : selectedIndices) {
                        ExtensionCard card = availableExtensions.get(index);
                        cardsToAdd.add(card);
                        selectedExtensions.add(card); // Track for configuration
                        System.out.println(" [+] " + card.getName());
                    }

                    model.getDeck().addExtensions(cardsToAdd);
                } else {
                    System.out.println("No extensions selected. Standard game.");
                }
            } else {
                // Show error and retry
                String errorMsg = ExtensionManager.getInvalidSelectionMessage(selectedIndices, playerCount);
                gameView.showInvalidExtensionMessage(errorMsg);
            }
        }
    }

    public void playGame() {
        GameVariant variant = model.getVariant();
        
        if (variant instanceof FullHandVariant) {
            playFullHandGame();
        } else {
            playStandardGame();
        }
    }
    
    /**
     * Plays the game using the Full Hand variant rules.
     */
    private void playFullHandGame() {
        // Create a single FullHandRound that manages the entire game
        FullHandRound fullHandRound = new FullHandRound(model.getPlayers(), model.getDeck());
        FullHandRoundController roundController = new FullHandRoundController(
            fullHandRound,
            roundView,
            viewFactory,
            model,
            gameView
        );

        int roundNumber = roundController.getRoundCounter();
        gameView.showRound(roundNumber);
        
        // Update header with round and variant info
        if (gameView instanceof view.gui.GameViewGUI) {
            ((view.gui.GameViewGUI) gameView).updateHeader(roundNumber, model.getVariant().getName());
        }

        // Display initial game state in GUI (support RoundViewGUI and RoundViewHybrid)
        ArrayList<Player> bots = new ArrayList<>();
        for (Player p : model.getPlayers()) {
            if (!(p instanceof model.players.HumanPlayer)) {
                bots.add(p);
            }
        }

        if (roundView instanceof view.gui.RoundViewGUI) {
            view.gui.RoundViewGUI guiRoundView = (view.gui.RoundViewGUI) roundView;
            guiRoundView.displayBots(bots, new ArrayList<>());
            guiRoundView.displayTrophies(model.getTrophies());
            guiRoundView.displayDeck(fullHandRound.getDeck().getRemainingCount());
        } else if (roundView instanceof view.hybrid.RoundViewHybrid) {
            view.hybrid.RoundViewHybrid hybrid = (view.hybrid.RoundViewHybrid) roundView;
            hybrid.displayBots(bots, new ArrayList<>());
            hybrid.displayTrophies(model.getTrophies());
            hybrid.displayDeck(fullHandRound.getDeck().getRemainingCount());
        }
        
        // Play the entire Full Hand game in one round
        roundController.playRound();
        
        // Ask to save after the game
        if (gameView.askSaveAfterRound()) {
            String saveName = gameView.askSaveName();
            SaveManager.save(model, saveName);
        }
        
        endGame();
    }
    
    /**
     * Plays the game using standard variant rules.
     */
    private void playStandardGame() {
        while (!model.getDeck().isEmpty()) {
            RoundController roundController = new RoundController(
                    new Round(model.getPlayers(), model.getDeck()),
                    roundView,
                    viewFactory
            );

            int roundNumber = roundController.getRoundCounter();
            gameView.showRound(roundNumber);
            
            // Update header with round and variant info
            if (gameView instanceof view.gui.GameViewGUI) {
                ((view.gui.GameViewGUI) gameView).updateHeader(roundNumber, model.getVariant().getName());
            }
            
            // Display initial game state in GUI (support RoundViewGUI and RoundViewHybrid)
            ArrayList<Player> bots = new ArrayList<>();
            for (Player p : model.getPlayers()) {
                if (!(p instanceof model.players.HumanPlayer)) {
                    bots.add(p);
                }
            }

            if (roundView instanceof view.gui.RoundViewGUI) {
                view.gui.RoundViewGUI guiRoundView = (view.gui.RoundViewGUI) roundView;
                guiRoundView.displayBots(bots, new ArrayList<>());
                guiRoundView.displayTrophies(model.getTrophies());
                guiRoundView.displayDeck(model.getDeck().getRemainingCount());
            } else if (roundView instanceof view.hybrid.RoundViewHybrid) {
                view.hybrid.RoundViewHybrid hybrid = (view.hybrid.RoundViewHybrid) roundView;
                hybrid.displayBots(bots, new ArrayList<>());
                hybrid.displayTrophies(model.getTrophies());
                hybrid.displayDeck(model.getDeck().getRemainingCount());
            }
            
            roundController.playRound();

            if (!model.getDeck().isEmpty() && gameView.askSaveAfterRound()) {
                String saveName = gameView.askSaveName();
                SaveManager.save(model, saveName);
            }

            if (model.getDeck().isEmpty())
                break;
        }
        endGame();
    }

    /**
     * Saves current game configuration for restart functionality.
     * This should be called after all game setup is complete.
     */
    public void saveGameConfiguration() {
        // Use the tracked selected extensions
        ArrayList<ExtensionCard> extensionsToSave = selectedExtensions != null ? selectedExtensions : new ArrayList<>();
        
        this.gameConfiguration = new GameConfiguration(
            model.getPlayers(),
            model.getVariant(),
            extensionsToSave
        );
    }
    
    /**
     * Gets the saved game configuration.
     * @return the game configuration, or null if not saved
     */
    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    public void endGame() {
        for (Player player : model.getPlayers()) {
            if (player.getOffer() != null) {
                player.takeRemainingOfferCard();
            }
        }

        gameView.showEndRoundMessage();
        model.assignTrophies();
        model.calculateAllScores();

        for (Player player : model.getPlayers()) {
            gameView.showScore(player);
        }

        // Save configuration for restart before showing winners
        if (gameConfiguration != null) {
            GameConfigurationManager.saveConfiguration(gameConfiguration);
        }

        ArrayList<Player> winners = model.getWinners();
        gameView.showWinners(winners);
    }
}