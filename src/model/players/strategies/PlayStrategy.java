package model.players.strategies;

import model.cards.Card;
import model.players.Jest;
import model.players.Offer;

 import java.io.Serializable;
import java.util.ArrayList;

public interface PlayStrategy extends Serializable {
    Card[] setCardsToOffer(ArrayList<Card> hand);
    Offer chooseCard(ArrayList<Offer> availableOffers);
    void updateJest(Jest jest);
}
