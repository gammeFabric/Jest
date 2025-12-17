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
        if (hand.isEmpty()) {
            return null;
        }
        
        // On garde la logique de base : offrir la carte la plus "faible" face visible.
        // On sécurise juste les comparaisons pour éviter les crashs avec les Extensions.
        
        Card minCard = hand.get(0);
        Card otherCard = hand.get(1);

        // Compare suit values (Extension = 0, SuitCards = 1-4)
        if (minCard.getSuitValue() > otherCard.getSuitValue()) {
            minCard = otherCard;
        } 
        else if (minCard.getSuitValue() == otherCard.getSuitValue()) {
            // Si même "couleur" (ex: 2 Extensions ou 2 Trèfles)
            // On regarde la valeur faciale
            if (minCard instanceof SuitCard && otherCard instanceof SuitCard) {
                Suit s = ((SuitCard) minCard).getSuit();
                // Inversion pour les rouges (Carreau/Cœur) car "faible" = "grande valeur négative"
                if (s == Suit.DIAMONDS || s == Suit.HEARTS) {
                     if (minCard.getFaceValue() < otherCard.getFaceValue()) minCard = otherCard;
                } else {
                     if (minCard.getFaceValue() > otherCard.getFaceValue()) minCard = otherCard;
                }
            } else {
                // Comparaison simple pour les extensions
                if (minCard.getFaceValue() > otherCard.getFaceValue()) {
                    minCard = otherCard;
                }
            }
        }

        if (hand.isEmpty()) return null;

        Card faceUpCard = minCard;
        hand.remove(faceUpCard);
        Card faceDownCard = hand.get(0); // La carte restante
        hand.remove(faceDownCard);
        
        return new Card[] { faceUpCard, faceDownCard };
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