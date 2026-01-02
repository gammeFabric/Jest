package model.players;

import model.cards.Card;

 import java.io.Serializable;
import java.util.ArrayList;

public class Jest implements Serializable {
    private static final long serialVersionUID = 1L;
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
        for (Card card : cards) {
            visitor.visit(card);
        }
        if (visitor instanceof ScoreVisitorImpl) {
            ((ScoreVisitorImpl) visitor).countJestScore(this);
        }
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
