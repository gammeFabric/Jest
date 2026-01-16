package controller;


import model.players.HumanPlayer;
import model.players.Player;
import model.players.VirtualPlayer;
import view.interfaces.IPlayerView;
import view.interfaces.IHumanView;
import model.players.Offer;

import java.util.ArrayList;

/**
 * Contrôleur abstrait pour les actions des joueurs.
 * 
 * <p>Cette classe définit l'interface commune pour tous les contrôleurs
 * de joueurs et implémente le pattern Factory pour leur création.</p>
 * 
 * <p><b>Hiérarchie :</b></p>
 * <ul>
 *   <li>{@link HumanPlayerController} - Pour les joueurs humains</li>
 *   <li>{@link VirtualPlayerController} - Pour les joueurs virtuels (IA)</li>
 * </ul>
 * 
 * <p><b>Actions abstraites :</b></p>
 * <ul>
 *   <li><code>makeOffer()</code> - Créer une offre de cartes</li>
 *   <li><code>chooseCard()</code> - Choisir une carte depuis les offres</li>
 * </ul>
 * 
 * @see controller.HumanPlayerController
 * @see controller.VirtualPlayerController
 * @see model.players.Player
 */
public abstract class PlayerController {
    protected final Player player;
    protected final IPlayerView view;

    protected PlayerController(Player player, IPlayerView view) {
        this.player = player;
        this.view = view;
    }

    /**
     * Demande au joueur de constituer une offre à partir de ses cartes.
     *
     * <p>Une offre est composée d'une carte face visible et d'une carte face cachée.
     * Pour un joueur humain, la décision provient de la vue ; pour un joueur virtuel,
     * la décision provient de la stratégie.</p>
     *
     * @return l'offre construite pour ce tour (non {@code null} en situation normale)
     */
    public abstract Offer makeOffer();

    /**
     * Demande au joueur de choisir une offre parmi celles disponibles et, si applicable,
     * de sélectionner la carte face visible ou cachée.
     *
     * @param availableOffers offres actuellement sélectionnables
     * @return l'offre choisie (ou {@code null} si aucun choix n'est possible)
     */
    public abstract Offer chooseCard(ArrayList<Offer> availableOffers);

    /**
     * Fabrique le bon contrôleur de joueur en fonction du type réel du {@link Player}.
     *
     * @param player joueur à contrôler
     * @param view vue associée à ce joueur
     * @return une instance de {@link HumanPlayerController} ou {@link VirtualPlayerController}
     * @throws IllegalArgumentException si le type de joueur est inconnu ou si la vue n'est pas compatible
     */
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
