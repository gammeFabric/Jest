package controller;

import view.console.RoundView;
import model.players.Offer;
import model.players.Player;
import model.game.Round;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoundController {
    private Round model;
    private RoundView view;
    private final Map<Player, PlayerController> playerControllers;

    public RoundController(Round model, RoundView view) {
        this.model = model;
        this.view = view;
        this.playerControllers = new HashMap<>();
        initializePlayerControllers();
    }

    public void initializePlayerControllers() {
        for (Player player: model.getPlayers()){
            PlayerController controller = PlayerController.createController(player);
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
            PlayerController controller = getController(currentPlayer);
            Offer takenOffer = controller.chooseCard(model.getAvailableOffers());
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
