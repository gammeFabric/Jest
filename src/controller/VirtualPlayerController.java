package controller;

import model.players.Offer;
import model.players.Player;
import model.players.VirtualPlayer;
import view.interfaces.IPlayerView;

import java.util.ArrayList;

/**
 * Contrôleur pour les joueurs virtuels (IA).
 * 
 * <p>Cette classe délègue les décisions de jeu à la stratégie d'IA
 * associée au joueur virtuel.</p>
 * 
 * <p><b>Fonctionnement :</b></p>
 * <ul>
 *   <li>Délégation des décisions à la stratégie du joueur</li>
 *   <li>Affichage des actions via la vue</li>
 *   <li>Pas d'interaction utilisateur requise</li>
 * </ul>
 * 
 * <p><b>Stratégies disponibles :</b></p>
 * <ul>
 *   <li>{@link model.players.strategies.RandomStrategy} - Choix aléatoires</li>
 *   <li>{@link model.players.strategies.AggressiveStrategy} - Maximisation des points</li>
 *   <li>{@link model.players.strategies.CautiousStrategy} - Minimisation des risques</li>
 * </ul>
 * 
 * @see controller.PlayerController
 * @see model.players.VirtualPlayer
 * @see model.players.strategies.PlayStrategy
 */
public class VirtualPlayerController extends PlayerController {

    public VirtualPlayerController(Player player, IPlayerView view) {
        super(player, view);
    }

    @Override
    public Offer makeOffer() {
        VirtualPlayer virtualPlayer = (VirtualPlayer) player;
        Offer offer = virtualPlayer.makeOffer();

        
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
        
        Offer chosenOffer = virtualPlayer.chooseCard(availableOffers);

        
        if (chosenOffer != null) {
            view.showMessage(virtualPlayer.getName() + " has chosen a card from " + chosenOffer.getOwner().getName() + "'s offer.");
        }

        return chosenOffer;
    }
}
