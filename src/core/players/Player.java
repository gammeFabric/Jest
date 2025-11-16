package core.players;

import core.cards.Card;

import java.util.ArrayList;

public abstract class Player {
    protected String name;
    protected ArrayList<Card> hand;
    protected Jest jest;
    protected Offer offer;
    protected boolean isVirtual;
    protected int score;

    // nextPlayer test
//    protected Player nextPlayer;

    public Player(String name, boolean isVirtual) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.jest = new Jest();
        this.offer = null;
        this.isVirtual = isVirtual;
        this.score = 0;
        // nextPlayer test
//        this.nextPlayer = null;
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

    public boolean isVirtual() {
        return isVirtual;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    protected ArrayList<Card> getHand() {
        return hand;
    }

    public Card takeCardFromOffer(Offer offer, boolean takeFaceUp) {
        return offer.takeCard(takeFaceUp);
    }

    public abstract Offer makeOffer();
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers);


    // nextPlayer test

//    public void setNextPlayer(Player nextPlayer) {
//        this.nextPlayer = nextPlayer;
//    }
//
//    public Player getNextPlayer() {
//        return nextPlayer;
//    }

    public void addToHand(Card card){
        this.hand.add(card);
    }

    public Card getLastCard(){
        return this.jest.getCards().getLast();
    }

    public void takeRemainingOfferCard(){
        Card faceUp = offer.getFaceUpCard();
        Card faceDown = offer.getFaceDownCard();

        if (faceUp != null) {
            jest.addCard(faceUp);
            offer.setFaceUpCard(null);
        }

        if (faceDown != null) {
            jest.addCard(faceDown);
            offer.setFaceDownCard(null);
        }
    }

    public void calculateScore(ScoreVisitor visitor) {
        jest.accept(visitor);
        if (visitor instanceof ScoreVisitorImpl) {
            score = ((ScoreVisitorImpl) visitor).getTotalScore();
        }
    }

}
