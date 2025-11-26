package core.players.strategies;

import core.players.Offer;
import core.cards.Card;
import java.util.ArrayList;
import core.players.Jest;

public interface PlayStrategy {
    Card[] setCardsToOffer(ArrayList<Card> hand);
    Offer chooseCard(ArrayList<Offer> availableOffers);
    void updateJest(Jest jest);
}
