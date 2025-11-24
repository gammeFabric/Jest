package core.cards;

import core.players.Player;

import java.util.ArrayList;

public abstract class Card {
    protected boolean isTrophy;
    protected TrophyType trophyType;
    protected Suit trophySuit;
    protected Faces trophyFace;

    public Card(boolean isTrophy) {
        this.isTrophy = isTrophy;
        this.trophyType = TrophyType.NONE;
        this.trophyFace = null;
        this.trophySuit = null;
    }

    public boolean isTrophy(){
        return isTrophy;
    }

    public void setTrophy(boolean isTrophy){
        this.isTrophy = isTrophy;
    }

    public TrophyType getTrophyType(){
        return trophyType;
    }

    public void setTrophyType(TrophyType trophy){
        this.trophyType = trophy;
    }

    public void setTrophySuit(Suit suit){
        this.trophySuit = suit;
    }

    public void setTrophyFace(Faces trophyFace){
        this.trophyFace = trophyFace;
    }

    public Suit getTrophySuit(){
        return trophySuit;
    }

    public Faces getTrophyFace(){
        return trophyFace;
    }

    private boolean hasTrophySuit() {
        return trophySuit != null;
    }
    private boolean hasTrophyFace() {
        return trophyFace != null;
    }

    public String trophyInfo() {
        if (trophyType == null || trophyType == TrophyType.NONE) {
            return "Not a trophy";
        }
        if (hasTrophySuit()) {
            return trophyType + " of " + trophySuit;
        }

        if (hasTrophyFace()) {
            return trophyType + " of " + trophyFace;
        }

        return trophyType.toString();

    }

    public int getCardStrength(){
        int value = 0;
        if (this instanceof SuitCard) {
            value = this.getFaceValue();
            value += this.getSuitValue();
        }
        if (this instanceof Joker){
            value = 4;
            value += 4;
        }

        return value;
    }

    public abstract int getFaceValue();

    public abstract int getSuitValue();
}
