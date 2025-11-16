package core.game;

import core.cards.*;
import core.players.*;


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
        System.out.println("We can assign all trophies to get the winner");

        assignTrophies();
        calculateAllScores();
        for (Player player : players) {
            System.out.println(player.getName() + " has " + player.getScore() + " points.");
        }
        Player winner = getWinner();
        if (winner != null) {
            System.out.println("Winner is " + winner.getName());
        }
        else  {
            System.out.println("There is no winner");
        }

    }

    private Player getWinner(){
        int maxScore = -999;
        Player winner = null;
        for (Player player : players) {
            int score = player.getScore();
            if (score > maxScore) {
                maxScore = score;
                winner = player;
            }
        }
        return winner;
    }

    private void calculateAllScores() {
        ScoreVisitorImpl visitor = new ScoreVisitorImpl();
        for (Player player : players) {
            visitor.resetScore();
            player.calculateScore(visitor);
        }
    }

    // test assigning Trophies and choose best players

    private void assignTrophies() {
        if (trophies == null || trophies.isEmpty()) return;

        for (Card trophy : trophies) {
            Player winner = determineTrophyWinner(trophy);
            if (winner != null) {
                winner.getJest().addCard(trophy);
            }
        }
    }

    private Player determineTrophyWinner(Card trophy) {
        TrophyType tType  = trophy.getTrophyType();
        return switch (tType) {
            case HIGHEST_FACE -> evaluateHighestFace(trophy.getTrophySuit());
            case LOWEST_FACE -> evaluateLowestFace(trophy.getTrophySuit());
            case MAJORITY_FACE_VALUE -> evaluateMajorityFaceValue(trophy.getTrophyFace());
            case JOKER -> evaluateJokerTrophy();
            case BEST_JEST -> evaluateBestJest();
            case BEST_JEST_NO_JOKER -> evaluateBestJestWithoutJoker();
            default -> null;
        };
    }

    // test to check who is trophy winner

    private Player evaluateHighestFace(Suit suit) {
        int bestFaceValue = 0;
        Player best = null;
        ArrayList<Player> ties = new ArrayList<>();
        for (Player player : players) {
            for (Card card: player.getJest().getCards()){
                // МОЖЕТ БЫТЬ ОШИБКА + функционал если у нас будет больше карт
                if (suit.getStrength() == card.getSuitValue()){
                    int val = card.getFaceValue();
                    if (val > bestFaceValue) {
                        bestFaceValue = val;
                        best = player;
                        ties.clear();
                        ties.add(player);
                    }
                    else if (val == bestFaceValue){
                        if(!ties.contains(player)){
                            ties.add(player);
                        }
                    }
                }
            }
        }
        if (ties.isEmpty()) return null;
        if (ties.size() == 1) return ties.getFirst();

        // can be enhanced by adding more functionalities to break ties
        return ties.getFirst();
    }

    private Player evaluateLowestFace(Suit suit) {
        // 4 because it's max in faceValues in Faces enum
        int lowestFaceValue = 4;
        ArrayList<Player> ties = new ArrayList<>();
        for (Player player : players) {
            for (Card card: player.getJest().getCards()){
                if (suit.getStrength() == card.getSuitValue()){
                    int val = card.getFaceValue();
                    if (val < lowestFaceValue) {
                        lowestFaceValue = val;
                        ties.clear();
                        ties.add(player);
                    }
                    else if (val == lowestFaceValue){
                        if(!ties.contains(player)){
                            ties.add(player);
                        }
                    }
                }
            }
        }
        if (ties.isEmpty()) return null;
        if (ties.size() == 1) return ties.getFirst();

        // can be enhanced by adding more functionalities to break ties
        return ties.getFirst();
    }

    private Player evaluateMajorityFaceValue(Faces face) {
        int cardsCount = 0;
        int maxCardsCount = -1;
        ArrayList<Player> ties = new ArrayList<>();
        for (Player player : players) {
            for (Card card: player.getJest().getCards()){
                if(card.getFaceValue() == face.getFaceValue()){
                    cardsCount++;
                }
            }
            if (cardsCount > maxCardsCount){
                maxCardsCount = cardsCount;
                ties.clear();
                ties.add(player);
            }
            else if (cardsCount == maxCardsCount){
                if(!ties.contains(player)){
                    ties.add(player);
                }
            }
        }
        if (ties.isEmpty()) return null;
        if (ties.size() == 1) return ties.getFirst();

        return breakTieByStrongestSuitAmongFaceValue(ties, face);
    }

    private Player evaluateJokerTrophy() {
        for (Player player: players){
            for (Card card: player.getJest().getCards()) {
                if (card instanceof Joker)
                    return player;
            }
        }
        return null;
    }

    private Player evaluateBestJest() {
        ArrayList<Player> ties = new ArrayList<>();
        calculateAllScores();
        int maxScore = -999;
        for (Player player : players) {
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
                ties.clear();
                ties.add(player);
            }
            else if (player.getScore() == maxScore){
                if (!ties.contains(player)){
                    ties.add(player);
                }
            }

        }

        if (ties.isEmpty()) return null;
        if (ties.size() == 1) return ties.getFirst();


        return breakTieByHighestFaceValue(ties);
    }

    private Player evaluateBestJestWithoutJoker(){
        // test method maybe after add flag to check if player has a Joker or not
        ArrayList<Player> ties = new ArrayList<>();
        ArrayList<Player> candidates = new ArrayList<>(players);
        calculateAllScores();
        int maxScore = -999;
        for (Player player : players) {
            for (Card card: player.getJest().getCards()){
                if (card instanceof Joker){
                    candidates.remove(player);
                    break;
                }
            }
        }
        for (Player player : candidates){
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
                ties.clear();
                ties.add(player);
            }
            else if (player.getScore() == maxScore){
                if (!ties.contains(player)){
                    ties.add(player);
                }
            }

        }

        if (ties.isEmpty()) return null;
        if (ties.size() == 1) return ties.getFirst();


        return breakTieByHighestFaceValue(ties);
    }

    private Player breakTieByHighestFaceValue(ArrayList<Player> ties) {
        Player best = null;
        int bestFaceValue = -1;
        int bestSuitValue = -1;
        for (Player player : ties) {
            for (Card card: player.getJest().getCards()){
                if (card instanceof SuitCard){
                    int fv = card.getFaceValue();
                    int sv = card.getSuitValue();
                    if (fv > bestFaceValue || (fv == bestFaceValue && sv > bestSuitValue)) {
                        bestFaceValue = fv;
                        bestSuitValue = sv;
                        best = player;
                    }
                }
            }
        }


        return best;
    }

    private Player breakTieByStrongestSuitAmongFaceValue(ArrayList<Player> ties,  Faces face) {
        Player best = null;
        int bestStrength = -1;
        for (Player player : ties) {
            int playerBestRank = -1;
            for (Card card: player.getJest().getCards()){
                if (card instanceof SuitCard && ((SuitCard)card).getFaceValue() == face.getFaceValue()) {
                    int rank = card.getSuitValue();
                    playerBestRank = Math.max(playerBestRank, rank);
                }
            }
            if (playerBestRank > bestStrength) {
                best = player;
                bestStrength = playerBestRank;
            }
            // maybe if we have the same suits we can check for this
            else if (playerBestRank == bestStrength) {
                continue;
            }
        }
        return best;
    }






    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}
