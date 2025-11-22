package core.players;

import java.util.*;
import core.cards.Suit;
import core.cards.Faces;
import core.cards.Card;
import core.cards.SuitCard;
import core.cards.Joker;

public class ScoreVisitorImpl implements ScoreVisitor {
    private int totalScore;
    private boolean hasJoker;
    private int heartCount;
    private Map<Suit, List<Faces>> suitMap; // for black pairs and Aces

    public ScoreVisitorImpl() {
        resetScore();
    }

    public void resetScore() {
        totalScore = 0;
        hasJoker = false;
        heartCount = 0;
        suitMap = new HashMap<>();
        for (Suit suit : Suit.values()) {
            suitMap.put(suit, new ArrayList<>());
        }
    }

    @Override
    public int visit(Card card) {
        if (card instanceof Joker) {
            hasJoker = true;
            return 0; // Joker is 0, bonus will be added later
        } else if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            suitMap.get(suitCard.getSuit()).add(suitCard.getFaces());
            if (suitCard.getSuit() == Suit.HEARTS) heartCount++;
            return suitCard.getFaceValue(); // default card value
        }
        return 0;
    }

    public void countJestScore(Jest jest) {
        resetScore();
        for (Card card : jest.getCards()) {
            int val = visit(card);

            // Diamonds decrease points
            if (card instanceof SuitCard) {
                SuitCard sc = (SuitCard) card;
                if (sc.getSuit() == Suit.DIAMONDS) val *= -1;
            }
            totalScore += val;
        }
        applyAceRule();
        applyBlackPairBonus();
        applyJokerBonus();
        // we can add other rules here
    }

    // Verified
    private void applyAceRule() {
        // if we have solo Ace with solo suit in Jest, he turns into 5 points (1 + 4)
        for (Suit suit : Suit.values()) {
            // check if a player has a Joker to apply heart rules
            if (suit == Suit.HEARTS && !hasJoker) continue;
            List<Faces> faces = suitMap.get(suit);
            if (faces.contains(Faces.ACE) && faces.size() == 1) {
                totalScore += 4; // add 4 because visitor has already added 1 for an Ace
            }
        }
    }

    // Verified
    private void applyBlackPairBonus() {
        // Spade + Club with the same Face = +2
        List<Faces> spades = suitMap.get(Suit.SPADES);
        List<Faces> clubs = suitMap.get(Suit.CLUBS);
        for (Faces face: spades) {
            if (clubs.contains(face)) {
                totalScore += 2;
            }
        }
    }

    // Verified
    private void applyJokerBonus() {
        List<Faces> hearts = suitMap.get(Suit.HEARTS);
        if (hasJoker) {
            if (heartCount == 0) totalScore += 4; // bonus +4 if no Hearts
            else if (heartCount >= 1 && heartCount <= 3){ // 1, 2, 3 Hearts it reduces jest by face value of each card
                int heartsValue = 0;
                for (Faces face: hearts) {
                    heartsValue += face.getFaceValue();
                }
                totalScore -= heartsValue;
            }
            else if (heartCount == 4) { // 4 hearts increase jest by face value of each card
                int heartsValue = 0;
                for (Faces face: hearts) {
                    heartsValue += face.getFaceValue();
                }
                totalScore += heartsValue;
            }
        }
        // subtract Face value of each Heart if we don't have a Joker
        if (!hasJoker) {
            for (Faces face : suitMap.get(Suit.HEARTS)) {
                totalScore -= face.getFaceValue();
            }
        }
    }



    public int getTotalScore() {;
        return totalScore;
    }
}
