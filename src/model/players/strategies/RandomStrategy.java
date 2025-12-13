package model.players.strategies;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;

import java.util.ArrayList;
import java.util.Random;

public class RandomStrategy implements PlayStrategy {
    private final Random random = new Random();

    @Override
    public Offer makeOffer(Player player) {
        if (player.getHand().size() < 2) {
            player.view.hasNoEnoughCards(player.getName());
            return null;
        }

        int faceUpIndex = random.nextInt(2);
        int faceDownIndex = 1 - faceUpIndex;

        Card faceUpCard = player.getHand().get(faceUpIndex);
        Card faceDownCard = player.getHand().get(faceDownIndex);

        Offer playerOffer = new Offer(player,  faceUpCard, faceDownCard);
        player.setOffer(playerOffer);
        player.getHand().clear();
        return playerOffer;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers, Player player) {

        // Collect only complete offers
        ArrayList<Offer> completeOffers = new ArrayList<>();
        for (Offer offer : availableOffers) {
            if (offer != null && offer.isComplete()) {
                completeOffers.add(offer);
            }
        }

        if (completeOffers.isEmpty()) {
            return null; // no offers available
        }

        // Remove own offer unless this is the last player
        ArrayList<Offer> selectableOffers = new ArrayList<>();
        for (Offer o : completeOffers) {
            if (o.getOwner() != player) {
                selectableOffers.add(o);
            }
        }

        // If all remaining offers were removed → player must pick from own offer
        if (selectableOffers.isEmpty()) {
            for (Offer o : completeOffers) {
                if (o.getOwner() == player) {
                    selectableOffers.add(o);
                    break;
                }
            }
        }

        // If still empty → nothing to take
        if (selectableOffers.isEmpty()) {
            return null;
        }

        //  Randomly select one of the offers
        Offer chosenOffer = selectableOffers.get(random.nextInt(selectableOffers.size()));

        //  Randomly decide: take face-up (true) or face-down (false)
        boolean takeFaceUp = random.nextBoolean();

        Card card = takeFaceUp ? chosenOffer.getFaceUpCard()
                : chosenOffer.getFaceDownCard();

        // If that card is not available, automatically pick the other one
        if (card == null) {
            takeFaceUp = !takeFaceUp; // flip decision
            card = takeFaceUp ? chosenOffer.getFaceUpCard()
                    : chosenOffer.getFaceDownCard();
        }

        // If still null → offer is invalid (should not happen)
        if (card == null) {
            return null;
        }

        // Take the card
        chosenOffer.takeCard(takeFaceUp);

        // Add to Jest
        player.getJest().addCard(card);

        return chosenOffer;
    }
}
