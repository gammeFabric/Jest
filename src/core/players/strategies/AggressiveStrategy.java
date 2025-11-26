package core.players.strategies;

import core.cards.Card;
import core.players.Offer;
import java.util.ArrayList;

public class AggressiveStrategy implements PlayStrategy {
    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        // Implémentation pour une stratégie agressive
        // Par exemple, toujours offrir la carte avec la valeur la plus haute
        if (hand.isEmpty()) {
            return null;
        }
        Card maxCard = hand.get(0);
        for (Card card : hand) {
            if (card.getValue() > maxCard.getValue()) {
                maxCard = card;
            }
        }
        hand.remove(maxCard);
        return new Offer(maxCard, null);
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        // Implémentation pour une stratégie agressive
        // Par exemple, toujours choisir l'offre avec la carte face visible la plus haute
        if (availableOffers.isEmpty()) {
            return null;
        }
        Offer maxOffer = availableOffers.get(0);
        for (Offer offer : availableOffers) {
            if (offer.getFaceUpCard().getValue() > maxOffer.getFaceUpCard().getValue()) {
                maxOffer = offer;
            }
        }
        return maxOffer;
    }
}