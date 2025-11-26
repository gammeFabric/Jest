package core.players;

import java.util.ArrayList;

import core.cards.Card;
import core.players.strategies.PlayStrategy;

public class VirtualPlayer extends Player {
    private PlayStrategy strategy;

    public VirtualPlayer(String name) {
        super(name, true);
    }

    public void setStrategy(PlayStrategy strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public Offer makeOffer() {
        // Implémentation basée sur la stratégie
        Card[] offeredCards = strategy.setCardsToOffer(this.hand);
        Card faceUpCard = offeredCards[0], faceDownCard = offeredCards[1];
        return new Offer(this, faceUpCard, faceDownCard);
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        // Implémentation basée sur la stratégie
        return strategy.chooseCard(availableOffers);
    }


}
