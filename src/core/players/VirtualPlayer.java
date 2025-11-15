package core.players;

import java.util.ArrayList;

public class VirtualPlayer extends Player {
    public VirtualPlayer(String name, boolean isVirtual) {
        super(name, isVirtual);
    }

    @Override
    public Offer makeOffer() {
        return null;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        System.out.println("Test");
        return null;
    }


}
