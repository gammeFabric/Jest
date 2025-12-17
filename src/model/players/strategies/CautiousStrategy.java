package model.players.strategies;

import model.cards.*;
import model.players.Jest;
import model.players.Offer;
import java.util.ArrayList;

public class CautiousStrategy implements PlayStrategy {
    private Jest playerJest;

    // ... setCardsToOffer reste inchangé (logique de base) ...
    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        // (Copiez votre code existant pour setCardsToOffer ici)
        // Pour simplifier l'exemple, je ne le répète pas, mais il ne change pas fondamentalement.
        if (hand.isEmpty()) return null;
        // ... votre logique de tri ...
        Card faceUp = hand.get(0);
        hand.remove(faceUp);
        Card faceDown = hand.get(0);
        hand.remove(faceDown);
        return new Card[]{faceUp, faceDown};
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) return null;

        Offer bestOffer = null;
        int maxScore = Integer.MIN_VALUE;

        for (Offer offer : availableOffers) {
            Card faceUp = offer.getFaceUpCard();
            if (faceUp == null) continue;

            int score = evaluateCard(faceUp);
            if (score > maxScore) {
                maxScore = score;
                bestOffer = offer;
            }
        }
        
        // Action
        if (bestOffer != null && bestOffer.getFaceUpCard() != null) {
            playerJest.addCard(bestOffer.getFaceUpCard());
            bestOffer.setFaceUpCard(null);
        }
        return bestOffer;
    }

    private int evaluateCard(Card c) {
        // EXTENSIBILITÉ : On demande à la carte sa valeur pour une stratégie PRUDENTE
        if (c instanceof ExtensionCard) {
            return ((ExtensionCard) c).getAIValue(StrategyType.CAUTIOUS, playerJest);
        }

        // Logique classique pour les cartes standards
        if (c instanceof Joker) return -1000; // Le prudent déteste le Joker
        
        if (c instanceof SuitCard) {
            SuitCard sc = (SuitCard) c;
            int val = sc.getFaceValue();
            // Prudent : Pique/Trèfle (+) sont bons, Carreau/Cœur (-) sont mauvais
            if (sc.getSuit() == Suit.DIAMONDS || sc.getSuit() == Suit.HEARTS) return -val;
            return val;
        }
        return 0;
    }

    @Override
    public void updateJest(Jest jest) { this.playerJest = jest; }
}