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

/**
 * Contrôleur principal de la partie.
 * 
 * <p>Ce contrôleur orchestre l'ensemble d'une partie de Jest, de la
 * configuration initiale jusqu'au calcul des scores finaux.</p>
 * 
 * <p><b>Responsabilités principales :</b></p>
 * <ul>
 *   <li>Configuration de la partie (joueurs, variante, extensions)</li>
 *   <li>Sélection et validation de la variante de jeu</li>
 *   <li>Gestion de la séquence des tours</li>
 *   <li>Attribution des trophées et calcul des scores</li>
 *   <li>Coordination avec les vues pour l'affichage</li>
 * </ul>
 * 
 * <p><b>Flux d'exécution :</b></p>
 * <ol>
 *   <li>Sélection de la variante</li>
 *   <li>Ajout des joueurs</li>
 *   <li>Sélection des extensions (optionnel)</li>
 *   <li>Tirage des trophées</li>
 *   <li>Déroulement de la partie (tours successifs)</li>
 *   <li>Calcul final des scores et attribution des trophées</li>
 *   <li>Annonce du/des gagnant(s)</li>
 * </ol>
 * 
 * @see model.game.Game
 * @see model.game.GameVariant
 * @see view.interfaces.IGameView
 */
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

    /**
     * Configure la liste des joueurs à partir des informations collectées via la vue.
     *
     * <p>Pour chaque joueur, la vue décide s'il s'agit d'un humain ou d'un joueur virtuel,
     * et, dans ce dernier cas, la stratégie à utiliser.</p>
     */
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

    /**
     * Démarre une partie.
     *
     * <p>Si la partie n'est pas encore configurée (aucun joueur), cette méthode orchestre
     * la sélection de la variante, l'ajout des joueurs, la sélection des extensions, puis le
     * tirage des trophées. Elle déclenche ensuite le déroulement de la partie.</p>
     *
     * @throws IllegalStateException si le nombre de joueurs ne correspond pas aux contraintes
     *                               de la variante choisie
     */
    public void startGame(){
        if (model.getPlayers() == null || model.getPlayers().isEmpty()) {
            
            selectVariant();

            addPlayers();

            
            GameVariant variant = model.getVariant();
            int playerCount = model.getPlayers().size();
            if (playerCount < variant.getMinPlayers() || playerCount > variant.getMaxPlayers()) {
                throw new IllegalStateException(
                    "This variant supports " + variant.getMinPlayers() + 
                    " to " + variant.getMaxPlayers() + " players only."
                );
            }

            
            handleExtensions();

            model.chooseTrophies(model.getPlayers().size());
            gameView.showTrophies(model.trophiesInfo());
            
            
            saveGameConfiguration();
        }

        playGame();
    }

    
    private void selectVariant() {
        
        List<GameVariant> availableVariants = getAvailableVariants();
        
        
        GameVariant selectedVariant = gameView.askForVariant(availableVariants);
        
        
        model.setVariant(selectedVariant);
    }

    
    private List<GameVariant> getAvailableVariants() {
        List<GameVariant> variants = new ArrayList<>();
        variants.add(new StandardVariant());
        variants.add(new ReverseScoringVariant());
        variants.add(new FullHandVariant());
        return variants;
    }

    
    private void handleExtensions() {
        ArrayList<ExtensionCard> availableExtensions = ExtensionManager.getAvailableExtensions();
        boolean validSelection = false;

        while (!validSelection) {
            
            ArrayList<Integer> selectedIndices = gameView.askForExtensions(availableExtensions);

            
            int playerCount = model.getPlayers().size();

            if (ExtensionManager.isValidSelection(selectedIndices, playerCount)) {
                validSelection = true;

                
                selectedExtensions = new ArrayList<>();

                
                if (!selectedIndices.isEmpty()) {
                    ArrayList<ExtensionCard> cardsToAdd = new ArrayList<>();
                    System.out.println("Validation OK. Adding cards to the deck:");

                    for (int index : selectedIndices) {
                        ExtensionCard card = availableExtensions.get(index);
                        cardsToAdd.add(card);
                        selectedExtensions.add(card); 
                        System.out.println(" [+] " + card.getName());
                    }

                    model.getDeck().addExtensions(cardsToAdd);
                } else {
                    System.out.println("No extensions selected. Standard game.");
                }
            } else {
                
                String errorMsg = ExtensionManager.getInvalidSelectionMessage(selectedIndices, playerCount);
                gameView.showInvalidExtensionMessage(errorMsg);
            }
        }
    }

    /**
     * Lance la boucle principale de jeu en fonction de la variante choisie.
     *
     * <p>Le déroulement diffère selon la variante :
     * {@link FullHandVariant} déclenche un tour spécifique (distribution initiale complète),
     * alors que les autres variantes s'enchaînent sur des tours standards jusqu'à épuisement
     * du deck.</p>
     */
    public void playGame() {
        GameVariant variant = model.getVariant();
        
        if (variant instanceof FullHandVariant) {
            playFullHandGame();
        } else {
            playStandardGame();
        }
    }
    
    
    private void playFullHandGame() {
        
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
        
        
        if (gameView instanceof view.gui.GameViewGUI) {
            ((view.gui.GameViewGUI) gameView).updateHeader(roundNumber, model.getVariant().getName());
        }

        
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
        
        
        roundController.playRound();
        
        
        if (gameView.askSaveAfterRound()) {
            String saveName = gameView.askSaveName();
            SaveManager.save(model, saveName);
        }
        
        endGame();
    }
    
    
    private void playStandardGame() {
        while (!model.getDeck().isEmpty()) {
            RoundController roundController = new RoundController(
                    new Round(model.getPlayers(), model.getDeck()),
                    roundView,
                    viewFactory
            );

            int roundNumber = roundController.getRoundCounter();
            gameView.showRound(roundNumber);
            
            
            if (gameView instanceof view.gui.GameViewGUI) {
                ((view.gui.GameViewGUI) gameView).updateHeader(roundNumber, model.getVariant().getName());
            }
            
            
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
     * Sauvegarde la configuration de la partie.
     *
     * <p>La configuration inclut les joueurs, la variante et les extensions sélectionnées.</p>
     */
    public void saveGameConfiguration() {
        
        ArrayList<ExtensionCard> extensionsToSave = selectedExtensions != null ? selectedExtensions : new ArrayList<>();
        
        this.gameConfiguration = new GameConfiguration(
            model.getPlayers(),
            model.getVariant(),
            extensionsToSave
        );
    }
    
    
    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    /**
     * Termine la partie : finalise les dernières cartes, attribue les trophées, calcule les scores
     * et affiche les résultats via la vue.
     *
     * <p>Si une configuration a été mémorisée, elle est également sauvegardée afin de permettre
     * un redémarrage avec les mêmes paramètres.</p>
     */
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

        
        if (gameConfiguration != null) {
            GameConfigurationManager.saveConfiguration(gameConfiguration);
        }

        ArrayList<Player> winners = model.getWinners();
        gameView.showWinners(winners);
    }
}