package core.players.strategies;

import core.cards.Card;
import core.cards.SuitCard;
import core.cards.Suit;
import core.players.Jest;
import core.players.Offer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AggressiveStrategy implements PlayStrategy {
    private Map<Card, Integer> seenCards; // Seen cards and their last round of appearance
    private int currentRound;
    private boolean hasJoker;
    private boolean hasHearts;
    private Jest playerJest;

    public AggressiveStrategy() {
        this.seenCards = new HashMap<>();
        this.currentRound = 0;
        this.hasJoker = false;
        this.hasHearts = false;
    }

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) {
            return null;
        }

        // Find the strongest card to offer face-up to attract opponents
        Card maxCard = findMaxCard(hand);

        Card faceUpCard = maxCard;
        hand.remove(maxCard);

        // Choose a face-down card that is weak or already seen
        Card faceDownCard = findWeakestOrSeenCard(hand);
        hand.remove(faceDownCard);

        // Remember that these cards have been played (for future prediction)
        seenCards.put(faceUpCard, currentRound);
        seenCards.put(faceDownCard, currentRound);

        return new Card[] { faceUpCard, faceDownCard };
    }

    private Card findMaxCard(ArrayList<Card> hand) {
        Card maxCard = hand.get(0);
        for (Card card : hand) {
            // Priority to black cards (Clubs and Spades) and Aces
            boolean isMaxBlack = isBlackSuit(maxCard);
            boolean isCardBlack = isBlackSuit(card);

            if (isCardBlack && !isMaxBlack) {
                maxCard = card;
            } else if (isCardBlack == isMaxBlack) {
                // If same suit, compare values
                if (getAdjustedValue(card) > getAdjustedValue(maxCard)) {
                    maxCard = card;
                }
            }
        }
        return maxCard;
    }

    private Card findWeakestOrSeenCard(ArrayList<Card> hand) {
        Card weakestCard = hand.get(0);
        for (Card card : hand) {
            // Prefer red cards (Diamonds, Hearts) or already seen cards
            boolean isWeakestRed = isRedSuit(weakestCard);
            boolean isCardRed = isRedSuit(card);

            if (isCardRed && !isWeakestRed) {
                weakestCard = card;
            } else if (isCardRed == isWeakestRed) {
                // If same suit, compare values (weaker is better)
                if (getAdjustedValue(card) < getAdjustedValue(weakestCard)) {
                    weakestCard = card;
                }
            }
        }
        return weakestCard;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) {
            return null;
        }

        // Find the offer that maximizes potential gain
        Offer bestOffer = null;
        int bestScore = Integer.MIN_VALUE;

        for (Offer offer : availableOffers) {
            int score = evaluateOffer(offer);
            if (score > bestScore) {
                bestScore = score;
                bestOffer = offer;
            }
        }

        return bestOffer;
    }

    // Method call by the player to update the current Jest
    public void updateJest(Jest jest) {
        this.playerJest = jest;
        for (Card card : jest.getCards()) {
            if (hasHearts && hasJoker) {
                return; // No need to continue checking
            }
            if (card instanceof SuitCard) {
                SuitCard suitCard = (SuitCard) card;
                if (suitCard.getSuit() == Suit.HEARTS) {
                    hasHearts = true;
                }
            } else {
                // Joker case
                hasJoker = true;
            }
        }
    }

    private int evaluateOffer(Offer offer) {
        Card faceUpCard = offer.getFaceUpCard();
        int score = 0;

        // Evaluate the face-up card
        if (faceUpCard instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) faceUpCard;
            Suit suit = suitCard.getSuit();

            // Priority to black cards (Clubs, Spades)
            if (suit == Suit.CLUBS || suit == Suit.SPADES) {
                score += faceUpCard.getFaceValue() * 2; // Bonus for black cards
            }

            // Check if the card can form a pair with the Jest
            if ((suit == Suit.CLUBS || suit == Suit.SPADES) && canFormPair(faceUpCard)) {
                score += 2; // Bonus for pairs
            }

            // Avoid hearts if we don't have the Joker
            if (suit == Suit.HEARTS) {
                if (!hasJoker) {
                    score -= 3; // Penalty for hearts (except if Joker)
                } else {
                    score += 1; // If we have the Joker, hearts are less penalized
                }
            }

            // Bonus if the card is an Ace and no other card of its suit is in the Jest
            if (faceUpCard.getFaceValue() == 1 && isOnlyOfSuit(suitCard)) {
                score += 5;
            }
        } else {
            // Joker case
            if (!hasHearts) {
                score += 5; // The Joker is worth 5 points if we don't have a Heart
            }
        }

        // Penalty if the card has already been seen (less interesting)
        if (seenCards.containsKey(faceUpCard)) {
            score -= 1;
        }

        return score;
    }

    private boolean isBlackSuit(Card card) {
        if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            Suit suit = suitCard.getSuit();
            return suit == Suit.CLUBS || suit == Suit.SPADES;
        }
        return false; // Joker is not a black card
    }

    private boolean isRedSuit(Card card) {
        if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            Suit suit = suitCard.getSuit();
            return suit == Suit.HEARTS || suit == Suit.DIAMONDS;
        }
        return false; // Joker is not a red card
    }

    private int getAdjustedValue(Card card) {
        if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            Suit suit = suitCard.getSuit();
            if (suit == Suit.DIAMONDS || suit == Suit.HEARTS) {
                return - card.getFaceValue(); // Inversion of values (1 becomes 14, etc.)
            }
        }
        return card.getFaceValue();
    }

    private boolean canFormPair(Card card) {
        // Logic to check if the card can form a pair with the player's Jest
        for (Card jestCard : playerJest.getCards()) {
            if (jestCard.getFaceValue() == card.getFaceValue()) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnlyOfSuit(SuitCard card) {
        // Logic to check if the card is the only one of its suit in the Jest
        Suit suit = card.getSuit();
        for (Card jestCard : playerJest.getCards()) {
            if (jestCard instanceof SuitCard) {
                SuitCard jestSuitCard = (SuitCard) jestCard;
                if (jestSuitCard.getSuit() == suit && jestSuitCard != card) {
                    return false; // Found another card of the same suit
                }
            }
        }
        return true; // No other card of the same suit found
    }

    public void incrementRound() {
        this.currentRound++;
    }
}
