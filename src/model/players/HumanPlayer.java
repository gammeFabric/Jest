package model.players;

import model.cards.Card;

import java.util.ArrayList;

public class HumanPlayer extends Player {
    public HumanPlayer(String name, boolean isVirtual) {
        super(name, isVirtual);
    }

    public Offer makeOffer(int faceUpIndex, int faceDownIndex) {
        if (hand.size() < 2) {
            return null;
        }

        // Use the indices provided by the controller
        Card faceUpCard = hand.get(faceUpIndex);
        Card faceDownCard = hand.get(faceDownIndex);

        Offer playerOffer = new Offer(this, faceUpCard, faceDownCard);
        offer = playerOffer;
        
        // Remove both selected cards from hand (remove higher index first)
        int higherIndex = Math.max(faceUpIndex, faceDownIndex);
        int lowerIndex = Math.min(faceUpIndex, faceDownIndex);
        
        hand.remove(higherIndex);
        hand.remove(lowerIndex);
        
        return playerOffer;
    }

    public Offer makeOffer(int[] selectedIndices) {
        if (hand.size() < 2 || selectedIndices.length < 2) {
            return null;
        }

        // Use the two indices provided by the controller
        Card faceUpCard = hand.get(selectedIndices[0]);
        Card faceDownCard = hand.get(selectedIndices[1]);

        Offer playerOffer = new Offer(this, faceUpCard, faceDownCard);
        offer = playerOffer;
        
        // Remove both selected cards from hand
        hand.remove(selectedIndices[0]);
        if (selectedIndices[1] > selectedIndices[0]) {
            hand.remove(selectedIndices[1] - 1); // Adjust index after first removal
        } else {
            hand.remove(selectedIndices[1]);
        }
        
        return playerOffer;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers, Offer chosenOffer, boolean isFaceUp) {
        Card takenCard = isFaceUp ? chosenOffer.getFaceUpCard() : chosenOffer.getFaceDownCard();
        if (takenCard == null) {
            return null;
        }
        chosenOffer.takeCard(isFaceUp);
        this.jest.addCard(takenCard);
        return chosenOffer;
    }

    @Override
    public Offer makeOffer() {
        throw new UnsupportedOperationException("Controller must call makeOffer(faceUpIndex, faceDownIndex).");
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        throw new UnsupportedOperationException("Controller must call chooseCard(availableOffers, chosenOffer, isFaceUp).");
    }
}
