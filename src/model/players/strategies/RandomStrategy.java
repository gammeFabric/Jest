package model.players.strategies;

import model.cards.Card;
import model.players.Jest;
import model.players.Offer;

import java.util.ArrayList;
import java.util.Random;

public class RandomStrategy implements PlayStrategy {
    private Random random = new Random();
    private Jest playerJest;
    private boolean isFullHandVariant = false; 

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) {
            return null;
        }
        
        
        isFullHandVariant = hand.size() > 4;
        
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
        
        
        boolean chooseFaceUp;
        if (isFullHandVariant) {
            
            chooseFaceUp = random.nextDouble() < 0.6;
        } else {
            
            chooseFaceUp = random.nextInt(2) == 0;
        }

        if (chooseFaceUp) {
            playerJest.addCard(selectedOffer.getFaceUpCard());
            selectedOffer.setFaceUpCard(null);
        } else {
            playerJest.addCard(selectedOffer.getFaceDownCard());
            selectedOffer.setFaceDownCard(null);
        }

        return selectedOffer;
    }
}
