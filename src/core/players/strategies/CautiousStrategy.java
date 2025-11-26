package core.players.strategies;
import core.cards.Card;
import core.cards.SuitCard;
import core.cards.Suit;
import core.players.Offer;
import java.util.ArrayList;
public class CautiousStrategy implements PlayStrategy {

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) {
            return null;
        }
        // Compare the colors of the two cards
        // If they are the same color, offer the card with the lowest value
        // Otherwise, offer the card with the lowest color (Diamonds < Hearts < Clubs < Spades)
        Card minCard = hand.get(0);
        if (minCard.getSuitValue() > hand.get(1).getSuitValue()) {
            minCard = hand.get(1);
        } else if (minCard.getSuitValue() == hand.get(1).getSuitValue()) {

            // Test if the Card is a SuitCard and then get its suit
            // If the suit is DIAMONDS, the face value is reversed
            Suit minCardSuit = ((SuitCard) minCard).getSuit();
            if (minCardSuit == Suit.DIAMONDS || minCardSuit == Suit.HEARTS) {
                if (minCard.getFaceValue() < hand.get(1).getFaceValue()) {
                    minCard = hand.get(1);
                }
            } else {
                if (minCard.getFaceValue() > hand.get(1).getFaceValue()) {
                    minCard = hand.get(1);
                }
            }
        }
        Card faceUpCard = minCard;
        hand.remove(minCard);
        Card faceDownCard = hand.get(0);
        hand.remove(faceDownCard);
        return new Card[] { faceUpCard, faceDownCard };
    }
    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) {
            return null;
        }
        // Compare the visible cards of each offer and choose the one with the highest value
        // For that, we must firstly compare the colors, then the face values
        Offer bestOffer = availableOffers.get(0);
        for (Offer offer : availableOffers) {
            if (offer.getFaceUpCard().getSuitValue() > bestOffer.getFaceUpCard().getSuitValue()) {
                bestOffer = offer;
            } else if (offer.getFaceUpCard().getSuitValue() == bestOffer.getFaceUpCard().getSuitValue()) {
                Suit bestOfferSuit = ((SuitCard) bestOffer.getFaceUpCard()).getSuit();
                if (bestOfferSuit == Suit.DIAMONDS || bestOfferSuit == Suit.HEARTS) {
                    if (offer.getFaceUpCard().getFaceValue() < bestOffer.getFaceUpCard().getFaceValue()) {
                        bestOffer = offer;
                    }
                } else {
                    if (offer.getFaceUpCard().getFaceValue() > bestOffer.getFaceUpCard().getFaceValue()) {
                        bestOffer = offer;
                    }
                }
            }
        }
        return bestOffer;
    }
}