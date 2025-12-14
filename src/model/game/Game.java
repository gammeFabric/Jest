package model.game;


import model.cards.*;
import model.players.HumanPlayer;
import model.players.Player;
import model.players.ScoreVisitorImpl;
import model.players.VirtualPlayer;
import model.players.strategies.StrategyType;

import java.util.ArrayList;

public class Game {
    private final Deck deck;
    private final ArrayList<Player> players;
// saving rounds has no logic here because they reference the same object each time so we capture not info this way
    private final ArrayList<Round> rounds;

    // test that Game knows about cards
    private ArrayList<Card> trophies;


    public Game() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.rounds = new ArrayList<>();
        this.trophies = new ArrayList<>();
    }

    // add players
    public void addHumanPlayer(String name) {
        players.add(new HumanPlayer(name, false));
    }

    public void addVirtualPlayer(String name, StrategyType strategy) {
        players.add(new VirtualPlayer(name, strategy));
    }

    public ArrayList<Player> getPlayers() {
        return players;
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

    public ArrayList<Player> getWinners() {
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

    public void calculateAllScores() {
        ScoreVisitorImpl visitor = new ScoreVisitorImpl();
        for (Player player : players) {
            visitor.resetScore();
            player.calculateScore(visitor);
        }
    }

    // test assigning Trophies and choose best players

    public void assignTrophies() {
        if (this.trophies == null || this.trophies.isEmpty()) return;

        for (Card trophy : this.trophies) {
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
        // 4 because it's max in faceValues in Face enum
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

    public Deck getDeck(){
        return deck;
    }


    public void assignTrophyType() {
        for (Card trophy : trophies) {
            if (trophy instanceof Joker) {
                trophy.setTrophyType(TrophyType.BEST_JEST);
            }
            if (trophy instanceof SuitCard) {
                // Сердечки
                if (((SuitCard) trophy).getSuit() == Suit.HEARTS) {
                    trophy.setTrophyType(TrophyType.JOKER);
                }
                // Червы
                else if (((SuitCard) trophy).getSuit() == Suit.CLUBS) {
                    if (((SuitCard) trophy).getFace() != Face.TWO && ((SuitCard) trophy).getFace() != Face.THREE) {
                        if (((SuitCard) trophy).getFace() == Face.FOUR) {
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.SPADES);
//                            type.setSuit(Suit.SPADES);
                            trophy.setTrophyType(type);
                        } else {
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.SPADES);
//                            type.setSuit(Suit.SPADES);
                            trophy.setTrophyType(type);
                        }
                    } else {
                        if (((SuitCard) trophy).getFace() == Face.THREE) {
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.HEARTS);
//                            type.setSuit(Suit.HEARTS);
                            trophy.setTrophyType(type);
                        } else {
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.HEARTS);
//                            type.setSuit(Suit.HEARTS);
                            trophy.setTrophyType(type);
                        }
                    }
                    // Пики
                } else if (((SuitCard) trophy).getSuit() == Suit.SPADES) {
                    if (((SuitCard) trophy).getFace() != Face.THREE && ((SuitCard) trophy).getFace() != Face.TWO) {
                        if (((SuitCard) trophy).getFace() == Face.FOUR) {
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.CLUBS);
//                            type.setSuit(Suit.CLUBS);
                            trophy.setTrophyType(type);
                        } else {
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.CLUBS);
//                            type.setSuit(Suit.CLUBS);
                            trophy.setTrophyType(type);
                        }
                    } else {
                        if (((SuitCard) trophy).getFace() == Face.THREE) {
                            TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                            trophy.setTrophyFace(Face.TWO);
//                            type.setFace(Face.TWO);
                            trophy.setTrophyType(type);
                        } else {
                            TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                            trophy.setTrophyFace(Face.THREE);
//                            type.setFace(Face.THREE);
                            trophy.setTrophyType(type);
                        }
                    }
                }
                // Бубна
                else {
                    if (((SuitCard) trophy).getFace() == Face.FOUR) {
                        trophy.setTrophyType(TrophyType.BEST_JEST_NO_JOKER);
                    } else if (((SuitCard) trophy).getFace() == Face.ACE) {
                        TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                        trophy.setTrophyFace(Face.FOUR);
//                        type.setFace(Face.FOUR);
                        trophy.setTrophyType(type);
                    } else {
                        if (((SuitCard) trophy).getFace() == Face.TWO) {
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.DIAMONDS);
//                            type.setSuit(Suit.DIAMONDS);
                            trophy.setTrophyType(type);
                        } else {
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.DIAMONDS);
//                            type.setSuit(Suit.DIAMONDS);
                            trophy.setTrophyType(type);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Card> chooseTrophies(int playerCount){
        int trophiesCount = (playerCount == 3) ? 2 : 1;
        for (int i = 0; i < trophiesCount; i++) {
            Card trophy = deck.dealCard();
            trophy.setTrophy(true);
            trophies.add(trophy);
        }
        assignTrophyType();
        return trophies;
    }


    public String trophiesInfo() {
        StringBuilder sb = new StringBuilder();
        for (Card card : trophies) {
            sb.append(card).append(": ").append(card.trophyInfo()).append("\n");
        }
        return sb.toString();
    }
}
