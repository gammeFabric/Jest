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

        // Check if this is Full Hand variant (more than 2 cards)
        if (humanPlayer.getHand().size() > 2) {
            // Full Hand variant: choose two cards from many
            int[] selectedIndices = humanView.chooseTwoCardsForOffer(humanPlayer.getName(), humanPlayer.getHand());
            if (selectedIndices != null && selectedIndices.length >= 2) {
                // Store the cards before making the offer (cards will be removed from hand)
                Card card1 = humanPlayer.getHand().get(selectedIndices[0]);
                Card card2 = humanPlayer.getHand().get(selectedIndices[1]);
                
                // Let player decide which card should be face-up
                int faceUpChoice = humanView.chooseFaceUpCard(humanPlayer.getName(),
                        new ArrayList<Card>() {{
                            add(card1);
                            add(card2);
                        }});

                // Determine which card is face-up based on player choice
                Card faceUpCard, faceDownCard;
                if (faceUpChoice == 0) {
                    faceUpCard = card1;
                    faceDownCard = card2;
                } else {
                    faceUpCard = card2;
                    faceDownCard = card1;
                }

                // Create offer with the two selected cards
                Offer offer = humanPlayer.makeOffer(new int[]{selectedIndices[0], selectedIndices[1]});
                humanView.thankForChoosing(faceUpCard, faceDownCard);
                return offer;
            }
        } else {
            // Standard variant: choose one card to show face-up
            int faceUpIndex = humanView.chooseFaceUpCard(humanPlayer.getName(), humanPlayer.getHand());

            // Simple logic for second card (face down) - it's the other card
            int faceDownIndex = (faceUpIndex == 0) ? 1 : 0;

            Card faceUpCard = humanPlayer.getHand().get(faceUpIndex);
            Card faceDownCard = humanPlayer.getHand().get(faceDownIndex);

            // 2. Controller calls View to display feedback
            humanView.thankForChoosing(faceUpCard, faceDownCard);

            // 3. Controller calls Model to execute action
            return humanPlayer.makeOffer(faceUpIndex, faceDownIndex);
        }
        return null;
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
