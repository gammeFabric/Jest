package core.players;

import core.cards.Card;

public class Player {
    private String name;
    private Jest jest;
    private Offer offer;
    private boolean isVirtual;
    private int score;

    public Player(String name, Jest jest, Offer offer, boolean isVirtual) {
        this.name = name;
        this.jest = jest;
        this.offer = offer;
        this.isVirtual = isVirtual;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public Jest getJest() {
        return jest;
    }

    public Offer getOffer() {
        return offer;
    }

    public Card takeCardFromOffer(Offer offer, boolean takeFaceUp) {
        return offer.takeCard(takeFaceUp);
    }
}
