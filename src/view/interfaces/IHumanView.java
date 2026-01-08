package view.interfaces;

import model.cards.Card;
import model.players.Offer;

import java.util.ArrayList;

public interface IHumanView extends IPlayerView {
    int chooseFaceUpCard(String playerName, ArrayList<Card> hand);
    Offer chooseOffer(String playerName, ArrayList<Offer> selectableOffers);
    boolean chooseFaceUpOrDown();
    void thankForChoosing(Card faceUpCard, Card faceDownCard);
    
    // New method for Full Hand variant - choose 2 cards from many
    int[] chooseTwoCardsForOffer(String playerName, ArrayList<Card> hand);
}

