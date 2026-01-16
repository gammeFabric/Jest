package view.interfaces;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;

/**
 * Interface pour l'affichage d'un tour de jeu.
 * 
 * <p>Cette interface définit les méthodes pour afficher les différentes
 * étapes d'un tour de Jest, du début jusqu'à la fin.</p>
 * 
 * <p><b>Étapes affichées :</b></p>
 * <ul>
 *   <li>Début du tour et distribution</li>
 *   <li>Création des offres</li>
 *   <li>Détermination du joueur de départ</li>
 *   <li>Phase de sélection des cartes</li>
 *   <li>Cartes choisies</li>
 *   <li>Fin du tour</li>
 * </ul>
 * 
 * @see view.console.RoundView
 * @see view.gui.RoundViewGUI
 * @see view.hybrid.RoundViewHybrid
 */
public interface IRoundView {
    /**
     * Signale le début d'un tour.
     */
    void showRoundStart();

    /**
     * Signale la phase de distribution des cartes.
     */
    void showDealCards();

    /**
     * Signale la phase où les joueurs constituent leur offre.
     */
    void showMakeOffers();

    /**
     * Signale la phase de détermination du premier joueur.
     */
    void showDetermineStartingPlayer();

    /**
     * Affiche le joueur qui commence et la carte face visible qui l'a désigné.
     *
     * @param p joueur de départ
     * @param faceUpCard carte face visible associée à l'offre de {@code p}
     */
    void showStartingPlayer(Player p, Card faceUpCard);

    /**
     * Signale le début de la phase de choix des offres.
     */
    void showChoosingPhaseStart();

    /**
     * Indique quel joueur doit jouer maintenant.
     *
     * @param p joueur dont c'est le tour
     */
    void showTurn(Player p);

    /**
     * Affiche le résultat d'un choix de carte, ainsi que le prochain joueur.
     *
     * @param player joueur qui vient de prendre une carte
     * @param takenOffer offre choisie
     * @param next prochain joueur à jouer (souvent le propriétaire de l'offre choisie)
     */
    void showCardTaken(Player player, Offer takenOffer, Player next);

    /**
     * Affiche le résultat du dernier choix de carte du tour.
     *
     * @param player joueur qui vient de prendre la dernière carte
     * @param takenOffer offre choisie
     */
    void showLastCardTaken(Player player, Offer takenOffer);

    /**
     * Indique que le deck est vide (fin de partie pour les variantes concernées).
     */
    void showDeckEmpty();

    /**
     * Signale la fin du tour.
     */
    void showRoundEnd();

    /**
     * Indique qu'aucune offre n'est disponible (cas exceptionnel / variantes / erreurs).
     */
    void showNoOffers();
}

