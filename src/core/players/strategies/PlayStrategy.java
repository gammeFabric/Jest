package core.players.strategies;

import core.players.Offer;
import core.players.Player;

import java.util.ArrayList;

public interface PlayStrategy {
    public abstract Offer makeOffer(Player player);
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers, Player player);

}
