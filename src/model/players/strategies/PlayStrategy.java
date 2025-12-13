package model.players.strategies;

import model.players.Offer;
import model.players.Player;

import java.util.ArrayList;

public interface PlayStrategy {
    public abstract Offer makeOffer(Player player);
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers, Player player);

}
