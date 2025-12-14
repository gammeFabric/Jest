package model.players;

import model.cards.Card;

public interface ScoreVisitor {
    int visit(Card card);
}
