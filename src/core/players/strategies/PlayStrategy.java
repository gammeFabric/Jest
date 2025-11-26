package core.players.strategies;

import core.players.Offer;
import core.cards.Card;
import java.util.ArrayList;

public interface PlayStrategy {
    Card[] setCardsToOffer(ArrayList<Card> hand);
    Offer chooseCard(ArrayList<Offer> availableOffers);
}
