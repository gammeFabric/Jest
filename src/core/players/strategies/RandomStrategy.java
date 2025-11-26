package core.players.strategies;

import core.cards.Card;
import core.players.Offer;
import java.util.ArrayList;
import java.util.Random;

public class RandomStrategy implements PlayStrategy {
    private Random random = new Random();

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

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) {
            return null;
        }
        int index = random.nextInt(availableOffers.size());
        return availableOffers.get(index);
    }
}
