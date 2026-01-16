package view.interfaces;

import model.cards.ExtensionCard;
import model.game.GameVariant;
import model.players.Player;
import model.players.strategies.StrategyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface pour la vue de gestion globale d'une partie.
 * 
 * <p>Cette interface définit les méthodes pour afficher les informations
 * d'une partie et récupérer les décisions de l'utilisateur lors de la
 * configuration et du déroulement du jeu.</p>
 * 
 * <p><b>Responsabilités :</b></p>
 * <ul>
 *   <li>Configuration des joueurs et de la variante</li>
 *   <li>Gestion des extensions</li>
 *   <li>Affichage des résultats et du gagnant</li>
 *   <li>Gestion des sauvegardes</li>
 * </ul>
 * 
 * @see view.console.GameView
 * @see view.gui.GameViewGUI
 * @see view.hybrid.GameViewHybrid
 */
public interface IGameView {
    /**
     * Demande à l'utilisateur le nombre de joueurs pour la partie.
     *
     * @return le nombre de joueurs (incluant humains et virtuels)
     */
    int askNumberOfPlayers();

    /**
     * Demande le nom du joueur numéro {@code playerNumber} lors de la configuration.
     *
     * @param playerNumber index (1..N) du joueur à configurer
     * @return le nom saisi/choisi pour ce joueur
     */
    String askPlayerName(int playerNumber);

    /**
     * Demande la stratégie (IA) à utiliser pour un joueur virtuel.
     *
     * @param name nom du joueur virtuel
     * @return la stratégie sélectionnée
     */
    StrategyType askStrategy(String name);

    /**
     * Demande si le joueur identifié par {@code name} est un humain.
     *
     * @param name nom du joueur
     * @return {@code true} si le joueur doit être contrôlé par un humain, sinon {@code false}
     */
    boolean isHumanPlayer(String name);

    /**
     * Affiche la liste des joueurs enregistrés pour la partie.
     *
     * @param players joueurs de la partie
     */
    void showPlayers(List<Player> players);

    /**
     * Affiche le numéro du tour en cours.
     *
     * @param roundNumber numéro du tour (1..)
     */
    void showRound(int roundNumber);

    /**
     * Affiche la description des trophées tirés en début de partie.
     *
     * @param trophiesInfo description textuelle des trophées
     */
    void showTrophies(String trophiesInfo);

    /**
     * Affiche le score final d'un joueur.
     *
     * @param player joueur dont le score doit être affiché
     */
    void showScore(Player player);

    /**
     * Affiche le gagnant d'une partie (cas d'un seul gagnant).
     *
     * @param winner gagnant
     */
    void showWinner(Player winner);

    /**
     * Affiche le(s) gagnant(s) d'une partie.
     *
     * @param winners liste des gagnants (ex-aequo possibles)
     */
    void showWinners(List<Player> winners);

    /**
     * Affiche un message de fin de tour / fin de partie, avant le bilan final.
     */
    void showEndRoundMessage();

    /**
     * Demande à l'utilisateur de sélectionner les extensions à ajouter au deck.
     *
     * @param availableExtensions liste des extensions disponibles
     * @return indices des extensions sélectionnées (par rapport à {@code availableExtensions})
     */
    ArrayList<Integer> askForExtensions(ArrayList<ExtensionCard> availableExtensions);

    /**
     * Affiche un message d'erreur expliquant pourquoi la sélection d'extensions est invalide.
     *
     * @param message message explicatif
     */
    void showInvalidExtensionMessage(String message);

    /**
     * Demande à l'utilisateur la variante de jeu à utiliser.
     *
     * @param availableVariants variantes proposées
     * @return la variante choisie
     */
    GameVariant askForVariant(List<GameVariant> availableVariants);

    /**
     * Demande si l'utilisateur souhaite sauvegarder après un tour.
     *
     * @return {@code true} si une sauvegarde doit être proposée, sinon {@code false}
     */
    boolean askSaveAfterRound();

    /**
     * Demande le nom de sauvegarde à utiliser.
     *
     * @return nom logique de la sauvegarde (sera normalisé par le gestionnaire de sauvegarde)
     */
    String askSaveName();

}

