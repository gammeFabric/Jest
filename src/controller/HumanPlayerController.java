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

        
        if (humanPlayer.getHand().size() > 2) {
            
            int[] selectedIndices = humanView.chooseTwoCardsForOffer(humanPlayer.getName(), humanPlayer.getHand());
            if (selectedIndices != null && selectedIndices.length >= 2) {
                
                Card card1 = humanPlayer.getHand().get(selectedIndices[0]);
                Card card2 = humanPlayer.getHand().get(selectedIndices[1]);
                
                
                int faceUpChoice = humanView.chooseFaceUpCard(humanPlayer.getName(),
                        new ArrayList<Card>() {{
                            add(card1);
                            add(card2);
                        }});

                
                Card faceUpCard, faceDownCard;
                if (faceUpChoice == 0) {
                    faceUpCard = card1;
                    faceDownCard = card2;
                } else {
                    faceUpCard = card2;
                    faceDownCard = card1;
                }

                
                Offer offer = humanPlayer.makeOffer(new int[]{selectedIndices[0], selectedIndices[1]});
                humanView.thankForChoosing(faceUpCard, faceDownCard);
                return offer;
            }
        } else {
            
            int faceUpIndex = humanView.chooseFaceUpCard(humanPlayer.getName(), humanPlayer.getHand());

            
            int faceDownIndex = (faceUpIndex == 0) ? 1 : 0;

            Card faceUpCard = humanPlayer.getHand().get(faceUpIndex);
            Card faceDownCard = humanPlayer.getHand().get(faceDownIndex);

            
            humanView.thankForChoosing(faceUpCard, faceDownCard);

            
            return humanPlayer.makeOffer(faceUpIndex, faceDownIndex);
        }
        return null;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        HumanPlayer humanPlayer = (HumanPlayer) player;

        humanView.showMessage(humanPlayer.getName() + ", please choose a card from players' offers!");

        
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

        
        Offer chosenOffer = humanView.chooseOffer(humanPlayer.getName(), selectableOffers);

        
        boolean isFaceUp = humanView.chooseFaceUpOrDown();

        
        
        Offer result = humanPlayer.chooseCard(availableOffers, chosenOffer, isFaceUp);

        
        if (result == null) {
            humanView.showMessage("No such card available.");
        }

        return result;
    }
}
