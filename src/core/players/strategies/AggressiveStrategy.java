package core.players.strategies;

import core.cards.Card;
import core.players.Offer;
import core.players.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class AggressiveStrategy implements PlayStrategy {

    // highest strength card becomes FaceUp card
    @Override
    public Offer makeOffer(Player player) {
        if (player.getHand().size() < 2){
            player.view.hasNoEnoughCards(player.getName());
            return null;
        }


        Card first = player.getHand().get(0);
        Card second = player.getHand().get(1);

        Card faceUp, faceDown;
        if (first.getCardStrength() >=  second.getCardStrength()) {
            faceUp = first;
            faceDown = second;
        }
        else {
            faceUp = second;
            faceDown = first;
        }

        Offer playerOffer = new Offer(player, faceUp, faceDown);
        player.setOffer(playerOffer);
        player.getHand().clear();
        return playerOffer;
    }

    // Pick the FaceUp card from the highest FaceUp card offer if FaceUp card in the offer is higher than hidden card
    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers, Player player) {
        // We get all the complete offer excluding a player's offer who chooses a card
        ArrayList<Offer> completeOffers = new ArrayList<>();
        for (Offer offer : availableOffers) {
            if (offer != null && offer.isComplete() && offer.getOwner() != player) {
                completeOffers.add(offer);
            }
        }

        // If no other offers, take from own offer
        if (completeOffers.isEmpty()) {
            for (Offer offer : availableOffers) {
                if (offer.getOwner() == player) {
                    completeOffers.add(offer);
                    break;
                }
            }
        }

        // check for no bugs in offers occurred
        if (completeOffers.isEmpty()) {
            return null;
        }

        Offer chosenOffer = completeOffers.stream()
                .max(Comparator.comparingInt(o -> o.getFaceUpCard().getCardStrength()))
                .orElse(completeOffers.getFirst());


        // The bot takes FaceUp card if its strength is higher than hidden card
        boolean takeFaceUp = chosenOffer.getFaceUpCard().getCardStrength() >= chosenOffer.getFaceDownCard().getCardStrength();
        Card takenCard = takeFaceUp ? chosenOffer.getFaceUpCard() : chosenOffer.getFaceDownCard();

        // Take the card and add to Jest
        chosenOffer.takeCard(takeFaceUp);
        player.getJest().addCard(takenCard);


        return chosenOffer;
    }
}
