package view.interfaces;

/**
 * Interface commune pour les vues de joueur.
 * 
 * <p>Cette interface définit les méthodes minimales pour afficher
 * des messages à un joueur (humain ou virtuel).</p>
 * 
 * <p><b>Responsabilités :</b></p>
 * <ul>
 *   <li>Affichage de messages généraux</li>
 *   <li>Notification d'erreurs de configuration</li>
 * </ul>
 */
public interface IPlayerView {
    void showMessage(String message);
    void hasNoEnoughCards(String name);
}

