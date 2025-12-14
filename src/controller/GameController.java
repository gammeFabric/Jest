package controller;

import model.players.Player;
import model.players.strategies.StrategyType;
import model.game.Round;
import view.console.GameView;
import model.game.Game;
import view.console.RoundView;

import java.util.ArrayList;

public class GameController {
    private final Game model;
    private final GameView consoleView;

    public GameController(Game model, GameView consoleView) {
        this.model = model;
        this.consoleView = consoleView;
    }
//    private final GameView guiView;

    public void addPlayers(){
        int playerCount = consoleView.askNumberOfPlayers();
        for (int i = 1; i <= playerCount; i++) {
            String name = consoleView.askPlayerName(i);
            boolean isHuman = consoleView.isHumanPlayer(name);
            if  (isHuman) {
                model.addHumanPlayer(name);
            }
            else {
                StrategyType strategy = consoleView.askStrategy(name);
                model.addVirtualPlayer(name, strategy);
            }
        }
        consoleView.showPlayers(model.getPlayers());
    }

    public void startGame(){
        addPlayers();
        if (model.getPlayers().size() < 3 || model.getPlayers().size() > 4) {
            throw new IllegalStateException("Jest supports 3 or 4 players only.");
        }
        model.chooseTrophies(model.getPlayers().size());
        consoleView.showTrophies(model.trophiesInfo());

        playGame();

    }


    public void playGame() {
        while (!model.getDeck().isEmpty()) {
            RoundController roundController = new RoundController(new Round(model.getPlayers(), model.getDeck()), new RoundView());

            consoleView.showRound(roundController.getRoundCounter());
            roundController.playRound();
            // somehow add rounds to rounds in game
//            rounds.add(currentRound);

            //change to take a deck from model not from round (reference on the same object)
            if (model.getDeck().isEmpty())
                break;
        }
        endGame();
    }

    public void endGame() {
        for (Player player : model.getPlayers()) {
            player.takeRemainingOfferCard();
        }

        consoleView.showEndRoundMessage();
        model.assignTrophies();
        model.calculateAllScores();

        for (Player player : model.getPlayers()) {
            consoleView.showScore(player);
        }

//        Player winner = getWinner();
//        view.showWinner(winner);
        ArrayList<Player> winners = model.getWinners();
        consoleView.showWinners(winners);

    }
}
