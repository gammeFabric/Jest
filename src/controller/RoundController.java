package controller;

import view.interfaces.IRoundView;
import view.ViewFactory;
import model.players.Offer;
import model.players.Player;
import model.game.Round;
import view.gui.RoundViewGUI;
import view.hybrid.RoundViewHybrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour un tour de jeu standard.
 * 
 * <p>Cette classe gère le déroulement complet d'un tour de jeu classique,
 * depuis la distribution des cartes jusqu'au retour des cartes non choisies
 * dans le deck.</p>
 * 
 * <p><b>Phases d'un tour :</b></p>
 * <ol>
 *   <li><b>Distribution</b> - Chaque joueur reçoit 2 cartes</li>
 *   <li><b>Offres</b> - Chaque joueur crée une offre (1 carte visible, 1 cachée)</li>
 *   <li><b>Détermination du premier joueur</b> - Selon la carte visible la plus forte</li>
 *   <li><b>Phase de choix</b> - Tour par tour, chaque joueur choisit une carte</li>
 *   <li><b>Retour des cartes</b> - Cartes non choisies retournent au deck</li>
 * </ol>
 * 
 * <p><b>Gestion de l'ordre de jeu :</b></p>
 * <ul>
 *   <li>Le joueur qui prend une carte désigne le prochain joueur (propriétaire de l'offre choisie)</li>
 *   <li>Si ce joueur a déjà joué, on passe au joueur avec la carte visible la plus forte</li>
 * </ul>
 * 
 * @see model.game.Round
 * @see view.interfaces.IRoundView
 */
public class RoundController {
    private Round model;
    private IRoundView view;
    private final Map<Player, PlayerController> playerControllers;
    private final ViewFactory viewFactory;

    public RoundController(Round model, IRoundView view, ViewFactory viewFactory) {
        this.model = model;
        this.view = view;
        this.viewFactory = viewFactory;
        this.playerControllers = new HashMap<>();
        initializePlayerControllers();
    }

    /**
     * Crée et enregistre les contrôleurs de joueurs nécessaires pour ce tour.
     *
     * <p>Cette méthode utilise {@link ViewFactory} pour construire la vue associée à chaque
     * joueur, puis {@link PlayerController#createController(Player, view.interfaces.IPlayerView)}
     * pour obtenir le contrôleur adapté (humain/virtuel).</p>
     */
    public void initializePlayerControllers() {
        for (Player player: model.getPlayers()){
            view.interfaces.IPlayerView playerView = viewFactory.createPlayerView(player);
            PlayerController controller = PlayerController.createController(player, playerView);
            this.playerControllers.put(player, controller);
        }
    }

    private PlayerController getController(Player player) {
        return playerControllers.get(player);
    }

    public int getRoundCounter() {
        return Round.getRoundCounter();
    }

    private void makeOffersPhase(){
        for (Player player: model.getPlayers()){
            PlayerController controller = getController(player);
            model.addOffer(controller.makeOffer());

        }
    }



    /**
     * Déroule un tour standard complet.
     *
     * <p>Enchaîne : début de tour, distribution, création des offres, détermination du joueur
     * de départ, phase de choix, puis fin de tour ou fin de partie si le deck est vide.</p>
     */
    public void playRound(){
        view.showRoundStart();

        view.showDealCards();
        model.dealCards();

        view.showMakeOffers();
        makeOffersPhase();

        view.showDetermineStartingPlayer();

        Player startingPlayer  = model.determineStartingPlayer();
        view.showStartingPlayer(startingPlayer, startingPlayer.getOffer().getFaceUpCard());


        playChoosingPhase(startingPlayer);

        if (model.getDeck().isEmpty()){
            view.showDeckEmpty();
        }
        else{
            endRound();
        }

    }


    /**
     * Déroule la phase de choix des offres en respectant l'ordre de jeu.
     *
     * <p>L'ordre du prochain joueur dépend généralement du propriétaire de l'offre choisie.
     * Le modèle gère la logique de sélection du prochain joueur ({@link Round#getNextPlayer}).</p>
     *
     * @param startingPlayer joueur qui commence la phase de choix
     */
    public void playChoosingPhase(Player startingPlayer){
        view.showChoosingPhaseStart();
        Player currentPlayer = startingPlayer;
        int turns = 0;
        int maxTurns = model.getPlayers().size();
        model.setAlreadyPlayed(new ArrayList<>());
        while (turns < maxTurns) {
            view.showTurn(currentPlayer);

            ArrayList<Offer> availableOffers = model.getAvailableOffers();
            if (view instanceof RoundViewGUI gui) {
                gui.showChoosingContext(currentPlayer, availableOffers);
            } else if (view instanceof RoundViewHybrid hybrid) {
                hybrid.showChoosingContext(currentPlayer, availableOffers);
            }

            PlayerController controller = getController(currentPlayer);
            Offer takenOffer = controller.chooseCard(availableOffers);

            if (takenOffer != null) {
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

            model.getAlreadyPlayed().add(currentPlayer);
            if (model.getAlreadyPlayed().size() <= maxTurns - 1) {
                Player nextPlayer = model.getNextPlayer(model.getAlreadyPlayed(), takenOffer);
                view.showCardTaken(currentPlayer, takenOffer, nextPlayer);
                currentPlayer = nextPlayer;
            }
            else{
                model.setIsOver(true);
                view.showLastCardTaken(currentPlayer, takenOffer);
            }
            turns++;
        }
    }


    /**
     * Termine le tour : remet les cartes non choisies dans le deck et notifie la vue.
     */
    public void endRound(){
        model.returnRemainingCardsToDeck();
        model.setIsOver(true);
        view.showRoundEnd();
    }
}
