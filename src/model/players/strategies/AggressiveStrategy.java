package model.players.strategies;

import model.cards.*;
import model.players.Jest;
import model.players.Offer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AggressiveStrategy implements PlayStrategy {
    private Set<String> seenCards; 
    private boolean hasJoker;
    private boolean hasHearts;
    private Jest playerJest;
    private boolean isFullHandVariant = false; 
    @SuppressWarnings("unused")
    private List<Card> trophies; 

    public AggressiveStrategy() {
        this.seenCards = new HashSet<>();
        this.hasJoker = false;
        this.hasHearts = false;
        this.trophies = new ArrayList<>();
    }

    
    public void setTrophies(List<Card> trophies) {
        this.trophies = trophies;
        
        for (Card c : trophies) {
            seenCards.add(getCardId(c));
        }
    }

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) return null;

        
        
        isFullHandVariant = hand.size() > 4;

        Card bestCard = hand.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Card c : hand) {
            int score = evaluateCardPotential(c);
            if (score > bestScore) {
                bestScore = score;
                bestCard = c;
            }
        }

        Card faceDownCard = bestCard;
        hand.remove(faceDownCard);

        Card faceUpCard;
        if (isFullHandVariant) {
            
            
            faceUpCard = selectFaceUpCardForFullHand(hand);
        } else {
            
            faceUpCard = hand.get(0);
        }
        hand.remove(faceUpCard);

        return new Card[] { faceUpCard, faceDownCard };
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) return null;

        updateMemory(availableOffers);

        Offer bestOffer = null;
        double maxScore = -9999.0;

        for (Offer offer : availableOffers) {
            Card visible = offer.getFaceUpCard();
            if (visible == null) continue;

            double score = evaluateCardPotential(visible);
            
            
            if (isFullHandVariant) {
                score = adjustScoreForFullHandPhase(score, offer);
            }

            if (score > maxScore) {
                maxScore = score;
                bestOffer = offer;
            }
        }

        
        if (bestOffer == null) {
            for(Offer o : availableOffers) {
                if (o.getFaceUpCard() != null) {
                    bestOffer = o;
                    break;
                }
            }
        }

        if (bestOffer != null && bestOffer.getFaceUpCard() != null) {
            this.playerJest.addCard(bestOffer.getFaceUpCard());
            bestOffer.setFaceUpCard(null);
        }

        return bestOffer;
    }

    @Override
    public void updateJest(Jest jest) {
        this.playerJest = jest;
        this.hasJoker = false;
        this.hasHearts = false;
        
        for (Card card : jest.getCards()) {
            if (card instanceof SuitCard) {
                if (((SuitCard) card).getSuit() == Suit.HEARTS) hasHearts = true;
            } else if (card instanceof Joker) {
                hasJoker = true;
            }
        }
    }

    private int evaluateCardPotential(Card card) {
        if (card instanceof ExtensionCard) {
            if (playerJest == null) return 0;
            
            return ((ExtensionCard) card).getAIValue(StrategyType.AGGRESSIVE, playerJest);
        }

        
        if (card instanceof Joker) {
            if (!hasHearts) return 20;
            if (playerJest != null && isGrandSlamPossible()) return 15; 
            return 0; 
        }

        if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            int val = suitCard.getFaceValue();
            Suit suit = suitCard.getSuit();

            
            if (suit == Suit.HEARTS) {
                if (hasJoker && hasHearts) {
                    
                    if (playerJest != null && isGrandSlamPossible()) {
                        return 50; 
                    } else {
                        return -20; 
                    }
                }
                if (hasJoker && !hasHearts) return -100;
                return -5;
            }

            
            if (val == 1) { 
                if (isOnlyOfSuitInJest(suit)) return 6; 
                return 2;
            } 
            
            if ((suit == Suit.CLUBS || suit == Suit.SPADES) && canFormBlackPair(suitCard)) {
                return val + 5; 
            }
            
            if (suit == Suit.DIAMONDS) return -val;
            return val;
        }

        return 0;
    }

    
    private boolean isGrandSlamPossible() {
        
        
        
        long trashCards = playerJest.getCards().stream()
                .filter(c -> !(c instanceof Joker) && !isHeart(c))
                .count();
        
        if (trashCards >= 1) return false; 

        
        int[] heartFaces = {1, 2, 3, 4};
        for (int face : heartFaces) {
            String cardId = "HEARTS-" + face;
            
            
            boolean iHaveIt = playerJest.getCards().stream().anyMatch(c -> 
                c instanceof SuitCard && ((SuitCard)c).getSuit() == Suit.HEARTS && ((SuitCard)c).getFaceValue() == face
            );
            if (iHaveIt) continue;

            
            
            if (seenCards.contains(cardId)) {
                return false; 
            }
        }

        return true;
    }

    

    private boolean isHeart(Card c) {
        return c instanceof SuitCard && ((SuitCard) c).getSuit() == Suit.HEARTS;
    }

    private void updateMemory(ArrayList<Offer> offers) {
        for (Offer o : offers) {
            if (o.getFaceUpCard() != null) {
                seenCards.add(getCardId(o.getFaceUpCard()));
            }
        }
    }

    private String getCardId(Card c) {
        if (c instanceof ExtensionCard) return ((ExtensionCard)c).getName();
        if (c instanceof Joker) return "JOKER";
        SuitCard sc = (SuitCard) c;
        return sc.getSuit().toString() + "-" + sc.getFaceValue();
    }
    
    private boolean canFormBlackPair(SuitCard card) {
        if (playerJest == null) return false; 

        
        Suit targetSuit = (card.getSuit() == Suit.SPADES) ? Suit.CLUBS : Suit.SPADES;
        
        for (Card c : playerJest.getCards()) {
            if (c instanceof SuitCard) {
                SuitCard existing = (SuitCard) c;
                if (existing.getSuit() == targetSuit && existing.getFaceValue() == card.getFaceValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOnlyOfSuitInJest(Suit suit) {
        if (playerJest == null) return true; 

        
        for (Card c : playerJest.getCards()) {
            if (c instanceof SuitCard && ((SuitCard) c).getSuit() == suit) {
                return false; 
            }
        }
        return true; 
    }
    
    
    private Card selectFaceUpCardForFullHand(ArrayList<Card> hand) {
        if (hand.isEmpty()) return null;
        
        Card bestCard = hand.get(0);
        int bestScore = Integer.MIN_VALUE;
        
        for (Card c : hand) {
            
            int score = evaluateCardForFaceUp(c);
            if (score > bestScore) {
                bestScore = score;
                bestCard = c;
            }
        }
        
        return bestCard;
    }
    
    
    private int evaluateCardForFaceUp(Card card) {
        if (card instanceof ExtensionCard) {
            return ((ExtensionCard) card).getAIValue(StrategyType.AGGRESSIVE, playerJest) / 2;
        }
        
        if (card instanceof Joker) {
            return 5; 
        }
        
        if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            int val = suitCard.getFaceValue();
            Suit suit = suitCard.getSuit();
            
            
            if (suit == Suit.HEARTS) {
                if (hasJoker) return 8; 
                return 3; 
            }
            
            if (suit == Suit.DIAMONDS) return -val / 2; 
            
            
            if (val >= 3) return val - 2; 
            return val;
        }
        
        return 0;
    }
    
    
    private double adjustScoreForFullHandPhase(double baseScore, Offer offer) {
        
        int handSize = playerJest.getCards().size();
        
        
        if (handSize <= 2) {
            return baseScore * 1.5; 
        }
        
        
        if (handSize >= 4) {
            return baseScore * 0.8; 
        }
        
        
        return baseScore;
    }
}