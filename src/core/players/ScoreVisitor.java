package core.players;

import core.cards.Card;

public interface ScoreVisitor {
    int visit(Card card);
}
