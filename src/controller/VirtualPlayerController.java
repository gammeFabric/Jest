package controller;

import model.players.Offer;
import model.players.Player;
import model.players.VirtualPlayer;
import view.interfaces.IPlayerView;

import java.util.ArrayList;

public class VirtualPlayerController extends PlayerController {

    public VirtualPlayerController(Player player, IPlayerView view) {
        super(player, view);
    }

    @Override
    public Offer makeOffer() {
        VirtualPlayer virtualPlayer = (VirtualPlayer) player;
        Offer offer = virtualPlayer.makeOffer();

        // Optional: Virtual view can display what the virtual player did
        if (offer != null) {
            view.showMessage(virtualPlayer.getName() + " has made an offer: Face up: " + offer.getFaceUpCard());
        } else {
            view.hasNoEnoughCards(virtualPlayer.getName());
        }

        return offer;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        VirtualPlayer virtualPlayer = (VirtualPlayer) player;
        // Delegate card choice to the VirtualPlayer model (which uses its strategy)
        Offer chosenOffer = virtualPlayer.chooseCard(availableOffers);

        // Optional: Virtual view can display what the virtual player chose
        if (chosenOffer != null) {
            view.showMessage(virtualPlayer.getName() + " has chosen a card from " + chosenOffer.getOwner().getName() + "'s offer.");
        }

        return chosenOffer;
    }
}
