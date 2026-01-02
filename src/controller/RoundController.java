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


    public void endRound(){
        model.returnRemainingCardsToDeck();
        model.setIsOver(true);
        view.showRoundEnd();
    }
}
