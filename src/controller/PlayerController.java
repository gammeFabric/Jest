package controller;


import model.players.HumanPlayer;
import model.players.Player;
import model.players.VirtualPlayer;
import view.console.HumanView;
import view.console.PlayerView;
import model.players.Offer;
import view.console.VirtualView;

import java.util.ArrayList;

public abstract class PlayerController {
    protected final Player player;
    protected final PlayerView view;

    protected PlayerController(Player player, PlayerView view) {
        this.player = player;
        this.view = view;
    }

    public abstract Offer makeOffer();
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers);

    public static PlayerController createController(Player player) {
        if (player instanceof HumanPlayer) {
            return new HumanPlayerController(player, new HumanView());
        }
        if (player instanceof VirtualPlayer){
            return new VirtualPlayerController(player, new VirtualView());
        }
        throw new IllegalArgumentException("Unknown player type");
    }



}
