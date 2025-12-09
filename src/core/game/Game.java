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
// saving rounds has no logic here because they reference the same object each time so we capture not info this way
    private final ArrayList<Round> rounds;

    // test that Game knows about cards
    private ArrayList<Card> trophies;

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
        Player player = new HumanPlayer(name, false);
        players.add(player);
    }

    public void addVirtualPlayer(String name) {
        Player player = new VirtualPlayer(name, StrategyType.CAUTIOUS);
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
        this.trophies =  chooseTrophies(players.size());
        view.showTrophies(trophiesInfo());

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
                    if (((SuitCard) trophy).getFaces() != Faces.TWO && ((SuitCard) trophy).getFaces() != Faces.THREE) {
                        if (((SuitCard) trophy).getFaces() == Faces.FOUR) {
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
                        if (((SuitCard) trophy).getFaces() == Faces.THREE) {
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
                    if (((SuitCard) trophy).getFaces() != Faces.THREE && ((SuitCard) trophy).getFaces() != Faces.TWO) {
                        if (((SuitCard) trophy).getFaces() == Faces.FOUR) {
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
                        if (((SuitCard) trophy).getFaces() == Faces.THREE) {
                            TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                            trophy.setTrophyFace(Faces.TWO);
//                            type.setFace(Faces.TWO);
                            trophy.setTrophyType(type);
                        } else {
                            TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                            trophy.setTrophyFace(Faces.THREE);
//                            type.setFace(Faces.THREE);
                            trophy.setTrophyType(type);
                        }
                    }
                }
                // Бубна
                else {
                    if (((SuitCard) trophy).getFaces() == Faces.FOUR) {
                        trophy.setTrophyType(TrophyType.BEST_JEST_NO_JOKER);
                    } else if (((SuitCard) trophy).getFaces() == Faces.ACE) {
                        TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                        trophy.setTrophyFace(Faces.FOUR);
//                        type.setFace(Faces.FOUR);
                        trophy.setTrophyType(type);
                    } else {
                        if (((SuitCard) trophy).getFaces() == Faces.TWO) {
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






    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}
