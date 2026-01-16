package model.players.strategies;

import model.cards.*;
import model.players.Jest;
import model.players.Offer;

import java.util.ArrayList;

public class CautiousStrategy implements PlayStrategy {
    private Jest playerJest;
    private boolean isFullHandVariant = false; 

    
    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) {
            return null;
        }
        
        
        isFullHandVariant = hand.size() > 4;
        
        
        
        
        Card minCard = hand.get(0);
        Card otherCard = hand.get(1);

        
        if (minCard.getSuitValue() > otherCard.getSuitValue()) {
            minCard = otherCard;
        } 
        else if (minCard.getSuitValue() == otherCard.getSuitValue()) {
            
            
            if (minCard instanceof SuitCard && otherCard instanceof SuitCard) {
                Suit s = ((SuitCard) minCard).getSuit();
                
                if (s == Suit.DIAMONDS || s == Suit.HEARTS) {
                     if (minCard.getFaceValue() < otherCard.getFaceValue()) minCard = otherCard;
                } else {
                     if (minCard.getFaceValue() > otherCard.getFaceValue()) minCard = otherCard;
                }
            } else {
                
                if (minCard.getFaceValue() > otherCard.getFaceValue()) {
                    minCard = otherCard;
                }
            }
        }

        if (hand.isEmpty()) return null;

        Card faceUpCard = minCard;
        hand.remove(faceUpCard);
        Card faceDownCard = hand.get(0); 
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
            
            
            if (isFullHandVariant) {
                score = adjustScoreForFullHandPhase(score, offer);
            }
            
            if (score > maxScore) {
                maxScore = score;
                bestOffer = offer;
            }
        }
        
        
        if (bestOffer != null && bestOffer.getFaceUpCard() != null) {
            playerJest.addCard(bestOffer.getFaceUpCard());
            bestOffer.setFaceUpCard(null);
        }
        return bestOffer;
    }

    private int evaluateCard(Card c) {
        
        if (c instanceof ExtensionCard) {
            return ((ExtensionCard) c).getAIValue(StrategyType.CAUTIOUS, playerJest);
        }

        
        if (c instanceof Joker) return -1000; 
        
        if (c instanceof SuitCard) {
            SuitCard sc = (SuitCard) c;
            int val = sc.getFaceValue();
            
            if (sc.getSuit() == Suit.DIAMONDS || sc.getSuit() == Suit.HEARTS) return -val;
            return val;
        }
        return 0;
    }

    @Override
    public void updateJest(Jest jest) { 
        this.playerJest = jest; 
    }
    
    
    private int adjustScoreForFullHandPhase(int baseScore, Offer offer) {
        
        int handSize = playerJest.getCards().size();
        
        
        if (handSize <= 2) {
            return (int)(baseScore * 0.7); 
        }
        
        
        if (handSize >= 4) {
            return (int)(baseScore * 0.5); 
        }
        
        
        return baseScore;
    }
}