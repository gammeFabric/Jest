package core.cards;

public class SuitCard extends Card {
    private Suit suit;
    private Faces faces;


    public SuitCard(boolean isTrophy, Suit suit, Faces faces) {
        super(isTrophy);
        this.suit = suit;
        this.faces = faces;
    }

    public Suit getSuit() {
        return suit;
    }

    public Faces getFaces() {
        return faces;
    }

    public boolean isAce() {
        return faces == Faces.ACE;
    }
    public boolean isBlack() {
        return suit == Suit.CLUBS || suit == Suit.SPADES;
    }

    @Override
    public int getFaceValue() {
        switch (faces) {
            case ACE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            default:
                return 0;
        }
    }


    @Override
    public String toString() {
        return this.faces + " " + this.suit;
    }

}
