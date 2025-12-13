package model.players;

import model.players.strategies.*;

import java.util.ArrayList;

public class VirtualPlayer extends Player {

    private PlayStrategy strategy;

    public VirtualPlayer(String name, StrategyType type) {
        super(name, true);
        setStrategy(type);
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
    public Offer makeOffer(int faceUpIndex, int faceDownIndex) {
        throw new UnsupportedOperationException("Controller must call makeOffer().");
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers, Offer chosenOffer, boolean isFaceUp) {
        throw new UnsupportedOperationException("Controller must call chooseCard(availableOffers).");
    }

    @Override
    public Offer makeOffer() {
        return strategy.makeOffer(this);
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        return strategy.chooseCard(availableOffers, this);
    }


}
