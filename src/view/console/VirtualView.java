package view.console;

import model.cards.Card;

public class VirtualView extends PlayerView {
    // Show a message
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    public void hasNoEnoughCards(String name) {
        System.out.println(name + "doesn't have enough cards to make an offer");
    }

    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        System.out.println("Thank you. You have chosen " + faceUpCard + " as a faceUp card and " + faceDownCard + " as a faceDown card" );
    }

}
