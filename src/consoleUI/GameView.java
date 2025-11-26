package consoleUI;

import core.players.Player;
import core.cards.*;

import java.util.*;
import java.util.Scanner;

public class GameView {

    private final Scanner scanner;

    public GameView() {
        this.scanner = new Scanner(System.in);
    }

    // Ask for number of players
    public int askNumberOfPlayers() {
        while (true) {
            System.out.print("Enter the number of players (3-4): ");
            String input = scanner.nextLine();
            try {
                int playerCount = Integer.parseInt(input);
                if (playerCount >= 3 && playerCount <= 4) {
                    return playerCount;
                } else {
                    System.out.println("Please enter a number between 3 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Ask for a player's name
    public String askPlayerName(int playerNumber) {
        System.out.print("Enter name for player " + playerNumber + ": ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "Player" + playerNumber;
        }
        return name;
    }

    // Ask for player type
    public boolean isHumanPlayer(String name) {
        while (true) {
            System.out.print("Is " + name + " a Human or Virtual player? (H/V): ");
            String type = scanner.nextLine().trim().toUpperCase();
            if (type.equals("H")) return true;
            if (type.equals("V")) return false;
            System.out.println("Please enter 'H' for Human or 'V' for Virtual.");
        }
    }

    // Show list of players
    public void showPlayers(List<Player> players) {
        System.out.println("Players added: " + players.size());
        for (Player player : players) {
            System.out.println("- " + player.getName());
        }
    }

    // Show round info
    public void showRound(int roundNumber) {
        System.out.println("\n=========================");
        System.out.println("      ROUND " + roundNumber);
        System.out.println("=========================");
    }

    // Show trophies
    public void showTrophies(String trophiesInfo) {
        System.out.println("Trophies selected: ");
        System.out.println(trophiesInfo);
    }

    // Show player score
    public void showScore(Player player) {
        System.out.println(player.getName() + " has " + player.getScore() + " points.");
    }

    // Show winner
    public void showWinner(Player winner) {
        if (winner != null) {
            System.out.println("Winner is " + winner.getName());
        } else {
            System.out.println("There is no winner");
        }
    }

    public void showWinners(List<Player> winners) {
        if (winners == null || winners.isEmpty()) {
            System.out.println("There is no winner");
        } else if (winners.size() == 1) {
            System.out.println("Winner is " + winners.getFirst().getName());
        } else {
            System.out.print("It's a tie between: ");
            for (int i = 0; i < winners.size(); i++) {
                System.out.print(winners.get(i).getName());
                if (i < winners.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    public void showEndRoundMessage() {
        System.out.println("Game ended successfully.");
        System.out.println("We can assign all trophies to get the winner");
    }
}
