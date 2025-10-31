package core.players;

import core.cards.Card;

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
}
