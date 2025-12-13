package model.game;

import model.cards.Deck;
import model.players.Offer;
import model.players.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class Round {
    private ArrayList<Offer> offers;
    private ArrayList<Player> players;
    private boolean isOver;
    private static int roundCounter = 0;
    private final Deck deck;

    // test
    private ArrayList<Player> alreadyPlayed;


    public Round(ArrayList<Player> players, Deck deck) {
        this.players = players;
        this.offers = new ArrayList<>();
        this.deck = deck;
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

    public Player getNextPlayer(ArrayList<Player> alreadyPlayed, Offer takenOffer){
        Player nextPlayer = takenOffer.getOwner();
        if (alreadyPlayed.contains(nextPlayer)) {
            nextPlayer = compareFaceUpCards();
        }
        return nextPlayer;
    }

    public boolean isOver(){
        return isOver;
    }

    public Deck getDeck(){
        return deck;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getAlreadyPlayed() {
        return alreadyPlayed;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setAlreadyPlayed(ArrayList<Player> alreadyPlayed) {
        this.alreadyPlayed = alreadyPlayed;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }



    public void dealCards() {
        for (Player player : players) {
            player.addToHand(deck.dealCard());
            player.addToHand(deck.dealCard());
        }
    }

    public void addOffer(Offer offer) {
        offers.add(offer);
    }

    public Player determineStartingPlayer(){
        Offer bestOffer = findBestOffer(offers);
        if (bestOffer == null) {
            return players.getFirst();
        }
        return bestOffer.getOwner();
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
        Offer bestOffer = findBestOffer(offersToCompare);
        return bestOffer.getOwner();

    }


    public Offer findBestOffer(ArrayList<Offer> offers){
        return offers.stream()
                .filter(offer -> offer.getFaceUpCard() != null)
                .max(Comparator
                        .comparingInt((Offer o) -> o.getFaceUpCard().getFaceValue())
                        .thenComparingInt(o -> o.getFaceUpCard().getSuitValue())
                )
                .orElse(null);
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
}





