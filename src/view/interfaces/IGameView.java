package view.interfaces;

import model.cards.ExtensionCard;
import model.players.Player;
import model.players.strategies.StrategyType;

import java.util.ArrayList;
import java.util.List;

public interface IGameView {
    int askNumberOfPlayers();
    String askPlayerName(int playerNumber);
    StrategyType askStrategy(String name);
    boolean isHumanPlayer(String name);
    void showPlayers(List<Player> players);
    void showRound(int roundNumber);
    void showTrophies(String trophiesInfo);
    void showScore(Player player);
    void showWinner(Player winner);
    void showWinners(List<Player> winners);
    void showEndRoundMessage();
    ArrayList<Integer> askForExtensions(ArrayList<ExtensionCard> availableExtensions);
    void showInvalidExtensionMessage(String message);

    boolean askSaveAfterRound();
    String askSaveName();

}

