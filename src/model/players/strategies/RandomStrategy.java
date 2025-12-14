package model.players.strategies;

import model.cards.Card;
import model.players.Jest;
import model.players.Offer;

import java.util.ArrayList;
import java.util.Random;

public class RandomStrategy implements PlayStrategy {
    private Random random = new Random();
    private Jest playerJest;

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) {
            return null;
        }
        int index = random.nextInt(hand.size());
        Card faceUpCard = hand.remove(index);
        index = random.nextInt(hand.size());
        Card faceDownCard = hand.remove(index);
        return new Card[]{faceUpCard, faceDownCard};
    }

    public void updateJest(Jest jest) {
        this.playerJest = jest;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) {
            return null;
        }
        int index = random.nextInt(availableOffers.size());
        Offer selectedOffer = availableOffers.get(index);
        int faceUpOrDown = random.nextInt(2);

        if (faceUpOrDown == 0) {
            playerJest.addCard(selectedOffer.getFaceUpCard());
            selectedOffer.setFaceUpCard(null);
        } else {
            playerJest.addCard(selectedOffer.getFaceDownCard());
            selectedOffer.setFaceDownCard(null);
        }

        return selectedOffer;
    }
}
