package core.cards;

import core.players.Player;

public abstract class Card {
    protected boolean isTrophy;

    public Card(boolean isTrophy) {
        this.isTrophy = isTrophy;
    }

    public boolean isTrophy(){
        return isTrophy;
    }

    public void setTrophy(boolean isTrophy){
        this.isTrophy = isTrophy;
    }

    public abstract int getFaceValue();

    public abstract int getSuitValue();
}
