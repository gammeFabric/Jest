package model.players;

import view.console.PlayerView;
import model.cards.Card;

import java.util.ArrayList;

public abstract class Player {
    protected String name;
    protected ArrayList<Card> hand;
    protected Jest jest;
    protected Offer offer;
    protected boolean isVirtual;
    protected int score;

    public PlayerView view;


    public Player(String name, boolean isVirtual) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.jest = new Jest();
        this.offer = null;
        this.isVirtual = isVirtual;
        this.score = 0;
    }

    // getters
    public String getName() {
        return name;
    }

    public Jest getJest() {
        return jest;
    }

    public Offer getOffer() {
        return offer;
    }

    public int getScore() {
        return score;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Card getLastCard(){

        return this.jest.getCards().getLast();
    }

    // setters
    public void setScore(int score) {
        this.score = score;
    }

    public void addToHand(Card card){
        this.hand.add(card);
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
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

    // abstract methods
    public abstract Offer makeOffer(int faceUpIndex, int faceDownIndex);
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers, Offer chosenOffer, boolean isFaceUp);
    public abstract Offer makeOffer();
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers);
}
