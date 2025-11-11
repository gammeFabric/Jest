package core.players;

import java.util.ArrayList;
import core.cards.Card;

public class Jest {
    private ArrayList<Card> cards;

    public Jest() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void accept(ScoreVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
