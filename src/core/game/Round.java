package core.game;

import core.cards.Deck;
import core.players.Offer;
import core.players.Player;


import java.util.ArrayList;
import java.util.Comparator;

public class Round {
    private ArrayList<Offer> offers;
    private ArrayList<Player> players;
    private Player currentPlayer;
    private boolean isOver;
    private Deck deck;
    private static int roundCounter = 0;

    // test
    private ArrayList<Player> alreadyPlayed;


    public Round(ArrayList<Player> players,  Deck deck) {
        this.players = players;
        this.deck = deck;
        this.offers = new ArrayList<>();
        roundCounter++;
    }

    public static int getRoundCounter() {
        return roundCounter;
    }

    public ArrayList<Offer> getAvailableOffers() {
        ArrayList<Offer> availableOffers = new ArrayList<>();
        for (Offer offer : offers) {
            if (offer.isComplete()) {
                availableOffers.add(offer);
            }
        }
        return availableOffers;
    }

    public void playRound(){
        System.out.println("Round Started");
            if (deck.getTrophies().isEmpty()) {
                deck.chooseTrophies(players.size());
                deck.trophiesInfo();
            }

        System.out.println("Deal cards to players");
        dealCards();

        System.out.println("Players make offers");
        makeOffers();

        System.out.println("Determine starting player");
        Player startingPlayer  = determineStartingPlayer();

        System.out.println("Playing choosing phase");
        playChoosingPhase(startingPlayer);

        if (deck.isEmpty()){
            System.out.println("Deck is empty, finalizing round...");
        }
        else{
            endRound();
        }

    }

    public void dealCards() {
        for (Player player : players) {
            player.addToHand(deck.dealCard());
            player.addToHand(deck.dealCard());
        }
    }

    public void makeOffers() {
        for (Player player : players) {
            Offer offer = player.makeOffer();
            offers.add(offer);
        }
    }

    public Player determineStartingPlayer(){
        Offer bestOffer = offers.stream()
                .filter(offer -> offer.getFaceUpCard() != null)
                .max(Comparator
                        .comparingInt((Offer offer) -> offer.getFaceUpCard().getFaceValue()) // value comparator
                        .thenComparingInt(offer -> offer.getFaceUpCard().getSuitValue()) // tie-breaking by Suit
                )
                .orElse(null);

        if (bestOffer == null) {
            System.out.println("No valid offers found. Defaulting to first player.");
            return players.getFirst();
        }

        Player startingPlayer = bestOffer.getOwner();
        System.out.println("First to play: " + startingPlayer.getName() +
                " (face-up card: " + bestOffer.getFaceUpCard() + ")");
        return startingPlayer;
    }

    public void playChoosingPhase(Player startingPlayer){
        System.out.println("\n--- CHOOSING CARDS PHASE ---");
        Player currentPlayer = startingPlayer;
        int turns = 0;
        int maxTurns = players.size();
        alreadyPlayed = new ArrayList<>();
        while (turns < maxTurns) {
            System.out.println("\nIt's " + currentPlayer.getName() + "'s turn to choose a card.");
            Offer takenOffer = currentPlayer.chooseCard(getAvailableOffers());
            alreadyPlayed.add(currentPlayer);
            if (alreadyPlayed.size() <= maxTurns - 1) {
                Player nextPlayer = getNextPlayer(alreadyPlayed, takenOffer);
                // переместить выше sout так как если следующий игрок не тот у кого взяли может быть ошибка с выводом
                System.out.println(currentPlayer.getName() + " took " + currentPlayer.getLastCard() + " from " + takenOffer.getOwner().getName() +
                        " → next player: " + nextPlayer.getName());
                currentPlayer = nextPlayer;
            }
            else{
                isOver = true;
                System.out.println(currentPlayer.getName() + " took " + currentPlayer.getLastCard() +
                        " → from: " + takenOffer.getOwner().getName());
            }
            turns++;
        }
    }

    private Player getNextPlayer(ArrayList<Player> alreadyPlayed, Offer takenOffer){
        Player nextPlayer = takenOffer.getOwner();
        if (alreadyPlayed.contains(nextPlayer)) {
            nextPlayer = compareFaceUpCards();
        }
        return nextPlayer;
    }

    private Player compareFaceUpCards(){
        ArrayList<Player> playersToCompare = new ArrayList<>(this.players);
        ArrayList<Offer> offersToCompare = new ArrayList<>();
        for (Player player : players) {
            if (this.alreadyPlayed.contains(player)) {
                playersToCompare.remove(player);
            }
            else{
                offersToCompare.add(player.getOffer());
            }
        }

        Offer bestOffer = offersToCompare.stream()
                .filter(offer -> offer.getFaceUpCard() != null)
                .max(Comparator
                        .comparingInt((Offer offer) -> offer.getFaceUpCard().getFaceValue()) // value comparator
                        .thenComparingInt(offer -> offer.getFaceUpCard().getSuitValue()) // tie-breaking by Suit
                )
                .orElse(null);


        // if it's last player and only one offer to compare it prints this
        if (bestOffer == null) {
            System.out.println("No valid offers found. Defaulting to first player available.");
            return playersToCompare.getFirst();
        }

        return bestOffer.getOwner();

    }

    public void returnRemainingCardsToDeck(){
        for (Offer offer : offers) {
            if (offer.isComplete()) {
                continue;
            }
            if (offer.getFaceUpCard() != null) {
                deck.addCard(offer.getFaceUpCard());
                offer.setFaceUpCard(null);
            }
            if (offer.getFaceDownCard() != null) {
                deck.addCard(offer.getFaceDownCard());
                offer.setFaceDownCard(null);
            }
        }

    }

    public void endRound(){
        returnRemainingCardsToDeck();
        System.out.println("Round has ended");
        isOver = true;
    }

    public boolean isOver(){
        return isOver;
    }

    public Deck getDeck(){
        return deck;
    }
}



//    public void nextTurn();
//    public boolean isRoundComplete();
//    public Player getCurrentPlayer() {}
//    public void endRound();


