package core.players;

import consoleUI.VirtualView;
import core.players.strategies.*;


import java.util.ArrayList;

import core.cards.Card;
import core.players.strategies.PlayStrategy;

public class VirtualPlayer extends Player {

    private PlayStrategy strategy;
    private final VirtualView view;

    public VirtualPlayer(String name, StrategyType type) {
        super(name, true);
        setStrategy(type);
        this.view = new VirtualView();
    }

    public void setStrategy(StrategyType type) {
        switch (type) {
            case RANDOM -> strategy = new RandomStrategy();
            case AGGRESSIVE -> strategy = new AggressiveStrategy();
            case CAUTIOUS -> strategy = new CautiousStrategy();
            default -> strategy = new RandomStrategy();
        }
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
