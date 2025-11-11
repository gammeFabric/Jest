package core.cards;

public enum Faces {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4);

    private final int faceValue;

    Faces(int faceValue) {
        this.faceValue = faceValue;
    }

    public int getFaceValue() {
        return faceValue;
    }
}
