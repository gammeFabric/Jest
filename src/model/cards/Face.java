package model.cards;

public enum Face {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4);

    private final int faceValue;

    Face(int faceValue) {
        this.faceValue = faceValue;
    }

    public int getFaceValue() {
        return faceValue;
    }
}
