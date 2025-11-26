package core.game;

import consoleUI.GameView;
import consoleUI.RoundView;
import core.cards.*;
import core.players.*;
import core.players.strategies.StrategyType;


import java.util.ArrayList;

public class Game {
    private final Deck deck;
    private final ArrayList<Player> players;
    private final ArrayList<Round> rounds;

    // test that Game knows about cards
    private final ArrayList<Card> trophies;

    // test GameView
    private final GameView view;

    public Game() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.rounds = new ArrayList<>();
        // test cards
        this.trophies = new ArrayList<>();
        this.view = new GameView();
    }

    // add players
    public void addHumanPlayer(String name) {
        Player player = new HumanPlayer(name);
        players.add(player);
    }

    public void addVirtualPlayer(String name) {
        Player player = new VirtualPlayer(name, StrategyType.RANDOM);
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayers(){
        int playerCount = view.askNumberOfPlayers();
        for (int i = 1; i <= playerCount; i++) {
            String name = view.askPlayerName(i);
            boolean isHuman = view.isHumanPlayer(name);
            if  (isHuman) {
                addHumanPlayer(name);
            }
            else {
                addVirtualPlayer(name);
            }
        }
        view.showPlayers(players);
    }

    public void startGame(){
        addPlayers();
        if (players.size() < 3 || players.size() > 4) {
            throw new IllegalStateException("Jest supports 3 or 4 players only.");
        }
        deck.chooseTrophies(players.size());
        view.showTrophies(deck.trophiesInfo());

        playGame();

    }

    public void playGame() {
        while (!deck.isEmpty()) {
            Round currentRound = new Round(players, deck, new RoundView());
            view.showRound(Round.getRoundCounter());
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

        view.showEndRoundMessage();
        assignTrophies();
        calculateAllScores();

        for (Player player : players) {
            view.showScore(player);
        }

//        Player winner = getWinner();
//        view.showWinner(winner);
        ArrayList<Player> winners = getWinners();
        view.showWinners(winners);

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

    private ArrayList<Player> getWinners() {
        int maxScore = Integer.MIN_VALUE;
        ArrayList<Player> winners = new ArrayList<>();

        // Find the max score
        for (Player player : players) {
            int score = player.getScore();
            if (score > maxScore) {
                maxScore = score;
            }
        }

        // Collect all players with the max score
        for (Player player : players) {
            if (player.getScore() == maxScore) {
                winners.add(player);
            }
        }

        return winners;
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

    private Player evaluateMajorityFaceValue(Face face) {
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

    private Player breakTieByStrongestSuitAmongFaceValue(ArrayList<Player> ties,  Face face) {
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
