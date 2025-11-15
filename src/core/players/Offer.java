package core.players;

import core.cards.Card;
import java.util.ArrayList;

public class Offer {
    private Player owner;
    private Card faceUpCard;
    private Card faceDownCard;

    public Offer(Player owner, Card faceUpCard, Card faceDownCard) {
        this.owner = owner;
        this.faceUpCard = faceUpCard;
        this.faceDownCard = faceDownCard;
    }

    public Player getOwner() {
        return owner;
    }

    public Card takeCard(boolean takeFaceUp) {
        if (takeFaceUp) {
            Card takenCard = faceUpCard;
            faceUpCard = null; // Face-up card is taken
            return takenCard;
        } else {
            Card takenCard = faceDownCard;
            faceDownCard = null; // Face-down card is taken
            return takenCard;
        }
    }

    public boolean isComplete() {
        return faceUpCard != null && faceDownCard != null;
    }


    public Card getFaceUpCard() {return faceUpCard;}

    public Card getFaceDownCard() {return faceDownCard;}

    public void setFaceUpCard(Card faceUpCard) {
        this.faceUpCard = faceUpCard;
    }

    public void setFaceDownCard(Card faceDownCard) {
        this.faceDownCard = faceDownCard;
    }

    public ArrayList<Card> getOfferedCard(){
        ArrayList<Card> cards = new ArrayList<>();
        // test for get offered cards
        if (faceUpCard != null) cards.add(faceUpCard);
        if (faceDownCard != null) cards.add(faceDownCard);
        return cards;
    }

    @Override
    public String toString() {
        return faceUpCard + " and one hidden card";
    }
}
