package core.players.strategies;

import core.cards.Card;
import core.players.Offer;
import java.util.ArrayList;

public class CautiousStrategy implements PlayStrategy {

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        // Implémentation pour une stratégie prudente
        // Par exemple, toujours offrir la carte avec la valeur la plus basse
        if (hand.isEmpty()) {
            return null;
        }
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        // Implémentation pour une stratégie prudente
        // Par exemple, toujours choisir l'offre avec la carte face visible la plus basse
        if (availableOffers.isEmpty()) {
            return null;
        }
        Offer minOffer = availableOffers.get(0);
        for (Offer offer : availableOffers) {
            if (offer.getFaceUpCard().getValue() < minOffer.getFaceUpCard().getValue()) {
                minOffer = offer;
            }
        }
        return minOffer;
    }
}