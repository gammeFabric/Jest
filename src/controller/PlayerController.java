package controller;


import model.players.HumanPlayer;
import model.players.Player;
import model.players.VirtualPlayer;
import view.interfaces.IPlayerView;
import view.interfaces.IHumanView;
import model.players.Offer;

import java.util.ArrayList;

public abstract class PlayerController {
    protected final Player player;
    protected final IPlayerView view;

    protected PlayerController(Player player, IPlayerView view) {
        this.player = player;
        this.view = view;
    }

    public abstract Offer makeOffer();
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers);

    public static PlayerController createController(Player player, IPlayerView view) {
        if (player instanceof HumanPlayer) {
            if (!(view instanceof IHumanView)) {
                throw new IllegalArgumentException("HumanPlayer requires IHumanView");
            }
            return new HumanPlayerController(player, (IHumanView) view);
        }
        if (player instanceof VirtualPlayer){
            return new VirtualPlayerController(player, view);
        }
        throw new IllegalArgumentException("Unknown player type");
    }



}
