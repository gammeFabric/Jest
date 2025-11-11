package core.cards;

public class Joker extends Card {
    public Joker(boolean isTrophy) {
        super(isTrophy);
    }

    @Override
    public int getFaceValue() {
        return 0;
    }

    @Override
    public int getSuitValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "Joker";
    }
}
