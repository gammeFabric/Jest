package controller;

import app.SaveManager;
import model.cards.ExtensionCard;
import model.game.ExtensionManager;
import model.players.Player;
import model.players.strategies.StrategyType;
import model.game.Round;
import view.interfaces.IGameView;
import model.game.Game;
import view.interfaces.IRoundView;
import view.ViewFactory;

import java.util.ArrayList;

public class GameController {
    private final Game model;
    private final IGameView gameView;
    private final IRoundView roundView;
    private final ViewFactory viewFactory;

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
            addPlayers();

            // Validation: Jest supports 3 or 4 players only
            if (model.getPlayers().size() < 3 || model.getPlayers().size() > 4) {
                throw new IllegalStateException("Jest supports 3 or 4 players only.");
            }

            // Handle extensions before choosing trophies
            handleExtensions();

            model.chooseTrophies(model.getPlayers().size());
            gameView.showTrophies(model.trophiesInfo());
        }

        playGame();
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

                // 3. Add selected extensions to the deck
                if (!selectedIndices.isEmpty()) {
                    ArrayList<ExtensionCard> cardsToAdd = new ArrayList<>();
                    System.out.println("Validation OK. Adding cards to the deck:");

                    for (int index : selectedIndices) {
                        ExtensionCard card = availableExtensions.get(index);
                        cardsToAdd.add(card);
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
        while (!model.getDeck().isEmpty()) {
            RoundController roundController = new RoundController(
                    new Round(model.getPlayers(), model.getDeck()),
                    roundView,
                    viewFactory
            );

            gameView.showRound(roundController.getRoundCounter());
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

    public void endGame() {
        for (Player player : model.getPlayers()) {
            player.takeRemainingOfferCard();
        }

        gameView.showEndRoundMessage();
        model.assignTrophies();
        model.calculateAllScores();

        for (Player player : model.getPlayers()) {
            gameView.showScore(player);
        }

        ArrayList<Player> winners = model.getWinners();
        gameView.showWinners(winners);
    }
}