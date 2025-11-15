package core.cards;

public enum TrophyType {
    NONE("None"),
    HIGHEST_FACE("Highest Face"),
    LOWEST_FACE("Lowest Face"),
    MAJORITY_FACE_VALUE("Majority Face Value"),
    JOKER("Joker"),
    BEST_JEST("Best Jest"),
    BEST_JEST_NO_JOKER("Best Jest without Joker");



    private String name;

    TrophyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;

    }




}
