package model.players;

import view.console.HumanView;
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
        hand.clear();
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
