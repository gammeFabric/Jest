package controller;

import view.interfaces.IHumanView;
import model.players.Player;
import model.players.HumanPlayer;
import model.players.Offer;
import model.cards.Card;

import java.util.ArrayList;


public class HumanPlayerController extends PlayerController {
    private final IHumanView humanView;
    
    public HumanPlayerController(Player player, IHumanView view) {
        super(player, view);
        this.humanView = view;
    }

    @Override
    public Offer makeOffer() {
        HumanPlayer humanPlayer = (HumanPlayer) player;

        if (humanPlayer.getHand().size() < 2) {
            humanView.hasNoEnoughCards(humanPlayer.getName());
            return null;
        }

        // 1. Controller calls View to get input
        int faceUpIndex = humanView.chooseFaceUpCard(humanPlayer.getName(), humanPlayer.getHand());

        // Simple logic for the second card (face down) - it's the other card
        // This is business logic, but it's simpler to keep it here or delegate to a utility.
        // For simplicity, we keep it here:
        int faceDownIndex = (faceUpIndex == 0) ? 1 : 0;

        Card faceUpCard = humanPlayer.getHand().get(faceUpIndex);
        Card faceDownCard = humanPlayer.getHand().get(faceDownIndex);

        // 2. Controller calls View to display feedback
        humanView.thankForChoosing(faceUpCard, faceDownCard);

        // 3. Controller calls Model to execute the action
        return humanPlayer.makeOffer(faceUpIndex, faceDownIndex);
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        HumanPlayer humanPlayer = (HumanPlayer) player;

        humanView.showMessage(humanPlayer.getName() + ", please choose a card from players' offers!");

        // Logic to filter available offers (moved from HumanPlayer)
        ArrayList<Offer> selectableOffers = new ArrayList<>();
        ArrayList<Offer> completeOffers = new ArrayList<>();

        for (Offer offer : availableOffers) {
            if (offer != null && offer.isComplete()) {
                completeOffers.add(offer);
            }
        }

        if (completeOffers.isEmpty()) {
            humanView.showMessage("No available offers to choose from.");
            return null;
        }

        for (Offer offer : completeOffers) {
            if (offer.getOwner() != humanPlayer) {
                selectableOffers.add(offer);
            }
        }

        if (selectableOffers.isEmpty()) {
            for (Offer offer : completeOffers) {
                if (offer.getOwner() == humanPlayer) {
                    selectableOffers.add(offer);
                    humanView.showMessage("You are the last player — you must take a card from your own offer!");
                    break;
                }
            }
        }

        if (selectableOffers.isEmpty()) {
            humanView.showMessage("No available offers to choose from.");
            return null;
        }

        // 1. Controller calls View to get input (which offer)
        Offer chosenOffer = humanView.chooseOffer(humanPlayer.getName(), selectableOffers);

        // 2. Controller calls View to get input (face-up or face-down)
        boolean isFaceUp = humanView.chooseFaceUpOrDown();

        // 3. Controller calls Model to execute the action
        // The model method is simplified to only perform the final action.
        Offer result = humanPlayer.chooseCard(availableOffers, chosenOffer, isFaceUp);

        // Optional: Controller handles "No such card" message if model returns null
        if (result == null) {
            humanView.showMessage("No such card available.");
        }

        return result;
    }
}
