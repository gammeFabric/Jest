package core.game;

import core.cards.Card;
import core.cards.Deck;
import core.players.HumanPlayer;
import core.players.Player;
import core.players.VirtualPlayer;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private Deck deck;
    private Round currentRound;
    private ArrayList<Player> players;
    private ArrayList<Round> rounds;
    private Scanner scanner = new Scanner(System.in);

    // test that Game knows about cards
    private ArrayList<Card> trophies;


    public Game() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.rounds = new ArrayList<>();
        // test cards
        this.trophies = new ArrayList<>();
    }

    // add players
    public void addHumanPlayer(String name) {
        Player player = new HumanPlayer(name, false);
        players.add(player);
    }

    public void addVirtualPlayer(String name) {
        Player player = new VirtualPlayer(name, false);
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        int playerCount = 0;

        // Step 1: ask for number of players
        while (true) {
            System.out.print("Enter the number of players (3-4): ");
            String input = scanner.nextLine();
            try {
                playerCount = Integer.parseInt(input);
                if (playerCount >= 3 && playerCount <= 4) {
                    break;
                } else {
                    System.out.println("Please enter a number between 3 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // Step 2: ask for each player's name and type
        for (int i = 1; i <= playerCount; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                name = "Player" + i;
            }

            String type = "";
            while (true) {
                System.out.print("Is " + name + " a Human or Virtual player? (H/V): ");
                type = scanner.nextLine().trim().toUpperCase();
                if (type.equals("H") || type.equals("V")) {
                    break;
                }
                System.out.println("Please enter 'H' for Human or 'V' for Virtual.");
            }

            // Step 3: create the correct type of player
            if (type.equals("H")) {
                addHumanPlayer(name);
            } else {
                addVirtualPlayer(name);
            }
        }

        System.out.println("Players added: " + this.players.size());
    }

    public void startGame(){
        addPlayers();
        if (players.size() < 3 || players.size() > 4) {
            throw new IllegalStateException("Jest supports 3 or 4 players only.");
        }
        deck.chooseTrophies(players.size());
        System.out.println("Trophies selected: ");
        deck.trophiesInfo();

        playGame();

    }

    public void playGame() {
        while (!deck.isEmpty()) {
            currentRound = new Round(players, deck);
            System.out.println("\n=========================");
            System.out.println("      ROUND " + Round.getRoundCounter());
            System.out.println("=========================");

            currentRound.playRound();
            rounds.add(currentRound);
            if (currentRound.getDeck().isEmpty())
                break;
        }
        endGame();
    }

    public void endGame() {
        for (Player player : players) {
            player.takeRemainingOfferCard();
        }
        System.out.println("Game ended successfully.");
    }

    // test assigning Trophies and choose best players

//    private void assignTrophies() {
//        if (trophies == null || trophies.isEmpty()) return;
//
//        for (Card trophy : trophies) {
//            Player winner = determineTrophyWinner(trophy);
//            if (winner != null) {
//                winner.getJest().addCard(trophy);
//            }
//        }
//    }




    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}
