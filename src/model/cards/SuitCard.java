package model.cards;

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
        return faces.getFaceValue();
    }


    public int getSuitValue(){
        return suit.getStrength();
    }


    @Override
    public String toString() {
        return this.faces + " " + this.suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuitCard suitCard = (SuitCard) o;

        return suit == suitCard.suit &&
                faces == suitCard.faces;
    }

}
