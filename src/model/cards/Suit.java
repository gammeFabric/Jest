package model.cards;

public enum Suit {
    DIAMONDS(1),
    HEARTS(2),
    CLUBS(3),
    SPADES(4);

    private final int strength;
    Suit(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }
}
