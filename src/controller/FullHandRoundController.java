package controller;

import app.SaveManager;
import view.interfaces.IGameView;
import view.interfaces.IRoundView;
import view.interfaces.IPlayerView;
import view.ViewFactory;
import model.players.Offer;
import model.players.Player;
import model.game.FullHandRound;
import model.game.Game;
import model.game.Round;
import view.gui.RoundViewGUI;
import view.hybrid.RoundViewHybrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur spécialisé pour la variante "Full Hand".
 * 
 * <p>Cette classe gère la logique spécifique de la variante Full Hand où
 * toutes les cartes sont distribuées en début de partie et les joueurs
 * jouent jusqu'à n'avoir qu'une carte restante.</p>
 * 
 * <p><b>Différences avec le tour standard :</b></p>
 * <ul>
 *   <li>Distribution complète des cartes au début</li>
 *   <li>Choix de 2 cartes parmi toute la main pour l'offre</li>
 *   <li>Plusieurs tours d'offres jusqu'à la fin de la main</li>
 *   <li>Dernière carte automatiquement ajoutée au Jest</li>
 * </ul>
 * 
 * @see controller.RoundController
 * @see model.game.FullHandRound
 * @see model.game.variants.FullHandVariant
 */
public class FullHandRoundController {
    private final FullHandRound model;
    private final IRoundView view;
    private final IGameView gameView;
    private final Game gameModel;
    private final Map<Player, PlayerController> playerControllers;
    private final ViewFactory viewFactory;

    public FullHandRoundController(FullHandRound model, IRoundView view, ViewFactory viewFactory, Game gameModel, IGameView gameView) {
        this.model = model;
        this.view = view;
        this.viewFactory = viewFactory;
        this.gameModel = gameModel;
        this.gameView = gameView;
        this.playerControllers = new HashMap<>();
        initializePlayerControllers();
    }

    /**
     * Crée et enregistre les contrôleurs de joueurs nécessaires pour ce tour "Full Hand".
     *
     * <p>Cette méthode s'appuie sur {@link ViewFactory} pour construire la vue de chaque joueur,
     * puis utilise {@link PlayerController#createController(Player, IPlayerView)} pour obtenir
     * le contrôleur adapté (humain/virtuel).</p>
     */
    public void initializePlayerControllers() {
        for (Player player: model.getPlayers()){
            IPlayerView playerView = viewFactory.createPlayerView(player);
            PlayerController controller = PlayerController.createController(player, playerView);
            this.playerControllers.put(player, controller);
        }
    }

    private PlayerController getController(Player player) {
        return playerControllers.get(player);
    }

    
    /**
     * Déroule un tour complet pour la variante "Full Hand".
     *
     * <p>Distribue toutes les cartes au début, puis enchaîne des sous-tours (création des offres,
     * détermination du joueur de départ et phase de choix) jusqu'à ce que la manche soit terminée.</p>
     */
    public void playRound() {
        view.showRoundStart();

        
        view.showDealCards();
        model.distributeAllCards();

        
        showInitialDistribution(model.getInitialCardsPerPlayer());

        
        while (!model.isOver()) {
            playFullHandTurn();

            
            try {
                if (gameView != null && gameView.askSaveAfterRound()) {
                    String saveName = gameView.askSaveName();
                    SaveManager.save(gameModel, saveName);
                }
            } catch (Exception e) {
                
                System.err.println("Warning: failed to save game after full-hand turn: " + e.getMessage());
            }
        }

        
        model.finalizeRound();
        view.showRoundEnd();
    }

    
    private void playFullHandTurn() {
        view.showMakeOffers();
        makeOffersPhase();

        view.showDetermineStartingPlayer();
        Player startingPlayer = model.determineStartingPlayer();
        
        if (startingPlayer != null && startingPlayer.getOffer() != null) {
            view.showStartingPlayer(startingPlayer, startingPlayer.getOffer().getFaceUpCard());
            playChoosingPhase(startingPlayer);
        }

        
        returnRemainingOfferCards();

        
        if (model.isOver()) {
            showGameEnding();
        }
    }

    
    /**
     * Déroule la phase de choix des offres en respectant l'ordre de jeu (variante "Full Hand").
     *
     * @param startingPlayer joueur qui commence la phase de choix
     */
    public void playChoosingPhase(Player startingPlayer) {
        view.showChoosingPhaseStart();
        Player currentPlayer = startingPlayer;
        int turns = 0;
        int maxTurns = model.getPlayers().size();
        model.setAlreadyPlayed(new ArrayList<>());
        
        while (turns < maxTurns) {
            view.showTurn(currentPlayer);

            ArrayList<Offer> availableOffers = model.getAvailableOffers();
            if (availableOffers.isEmpty()) {
                break; 
            }

            if (view instanceof RoundViewGUI gui) {
                gui.showChoosingContext(currentPlayer, availableOffers);
            } else if (view instanceof RoundViewHybrid hybrid) {
                hybrid.showChoosingContext(currentPlayer, availableOffers);
            }

            PlayerController controller = getController(currentPlayer);
            Offer takenOffer = controller.chooseCard(availableOffers);

            if (takenOffer != null) {
                handleTakenOffer(currentPlayer, takenOffer);
            }

            model.getAlreadyPlayed().add(currentPlayer);
            
            if (model.getAlreadyPlayed().size() <= maxTurns - 1) {
                Player nextPlayer = model.getNextPlayer(model.getAlreadyPlayed(), takenOffer);
                view.showCardTaken(currentPlayer, takenOffer, nextPlayer);
                currentPlayer = nextPlayer;
            } else {
                view.showLastCardTaken(currentPlayer, takenOffer);
            }
            turns++;
        }
    }

    
    private void makeOffersPhase() {
        for (Player player: model.getPlayers()){
            PlayerController controller = getController(player);
            Offer offer = controller.makeOffer();
            if (offer != null) {
                model.addOffer(offer);
            } else {
                System.err.println("Warning: Player " + player.getName() + " returned null offer");
            }
        }
    }

    
    private void handleTakenOffer(Player currentPlayer, Offer takenOffer) {
        boolean isBotTurn = currentPlayer.isVirtual();
        if (view instanceof RoundViewGUI gui) {
            if (isBotTurn) {
                gui.flashChosenOffer(takenOffer);
            } else {
                gui.highlightChosenOffer(takenOffer);
            }
            
            gui.resetInteractionFlag();
        } else if (view instanceof RoundViewHybrid hybrid) {
            if (isBotTurn) {
                hybrid.flashChosenOffer(takenOffer);
            } else {
                hybrid.highlightChosenOffer(takenOffer);
            }
        }
    }

    
    private void showInitialDistribution(int cardsPerPlayer) {
        
        
        
    }

    
    private void returnRemainingOfferCards() {
        for (Player player : model.getPlayers()) {
            Offer offer = player.getOffer();
            if (offer != null) {
                
                if (offer.getFaceUpCard() != null) {
                    player.addToHand(offer.getFaceUpCard());
                }
                
                if (offer.getFaceDownCard() != null) {
                    player.addToHand(offer.getFaceDownCard());
                }
                
                player.setOffer(null);
            }
        }
    }

    
    private void showGameEnding() {
        
        
        view.showRoundEnd();
    }

    
    public FullHandRound getModel() {
        return model;
    }

    
    /**
     * Retourne le compteur de tour courant.
     *
     * <p>Le compteur est géré au niveau du modèle via un compteur statique dans {@link Round}.</p>
     *
     * @return numéro du tour courant
     */
    public int getRoundCounter() {
        return Round.getRoundCounter();
    }
}
