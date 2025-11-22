package core.players;

import consoleUI.HumanView;
import core.cards.Card;

import java.util.ArrayList;
import java.util.InputMismatchException;

public class HumanPlayer extends Player {
    private final HumanView view;
    public HumanPlayer(String name, boolean isVirtual) {
        super(name, isVirtual);
        this.view = new HumanView();
    }

    public Offer makeOffer(){
        if (hand.size() < 2) {
            view.hasNoEnoughCards(this.name);
            return null;
        }

        int faceUpIndex = view.chooseFaceUpCard(this.name, this.hand);
        int faceDownIndex = (faceUpIndex == 0) ? 1 : 0;
        Card faceUpCard = hand.get(faceUpIndex);
        Card faceDownCard = hand.get(faceDownIndex);

        view.thankForChoosing(faceUpCard, faceDownCard);

        Offer playerOffer = new Offer(this, faceUpCard, faceDownCard);
        offer = playerOffer;
        hand.clear();
        return playerOffer;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {

        view.showMessage(name + ", please choose a card from other players' offers: ");
        view.showMessage("Available offers (you can't take from your own offer): ");


        // We get all the complete offer including a player's offer who chooses a card
        ArrayList<Offer> completeOffers = new ArrayList<>();
        for (Offer offer : availableOffers) {
            if (offer != null && offer.isComplete()) {
                completeOffers.add(offer);
            }
        }

        // Check if we have available offer
        if (completeOffers.isEmpty()) {
            view.showMessage("No available offers to choose from.");
            return null;
        }

        // We make a list with selectableOffers
        ArrayList<Offer> selectableOffers = new ArrayList<>();
        for (Offer offer : completeOffers) {
            if (offer.getOwner() != this) {
                selectableOffers.add(offer);
            }
        }

        // Check if no others offers - so you have to take from your own offer
        if (selectableOffers.isEmpty()) {
            for (Offer offer : completeOffers) {
                if (offer.getOwner() == this) {
                    selectableOffers.add(offer);
                    view.showMessage("You are the last player — you must take a card from your own offer!");
                    break;
                }
            }
        }

        // Bug when we don't have any offer available to select from
        if (selectableOffers.isEmpty()) {
            view.showMessage("No available offers to choose from.");
            return null;
        }

        // Available offers print info
        Offer chosenOffer = view.chooseOffer(name, selectableOffers);


        // User makes a choice of a card

        boolean isFaceUp  = view.chooseFaceUpOrDown();

        Card takenCard = isFaceUp ? chosenOffer.getFaceUpCard() : chosenOffer.getFaceDownCard();

        if (takenCard == null) {
            view.showMessage("No such card available.");
            return null;
        }

        chosenOffer.takeCard(isFaceUp);
        this.jest.addCard(takenCard);

        return chosenOffer;
    }
    }
