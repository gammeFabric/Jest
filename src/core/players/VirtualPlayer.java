package core.players;

import consoleUI.VirtualView;
import core.players.strategies.*;


import java.util.ArrayList;

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
        return strategy.makeOffer(this);
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        return strategy.chooseCard(availableOffers, this);
    }


}
