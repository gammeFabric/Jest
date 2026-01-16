package view.console;

import model.cards.Card;

/**
 * Vue console pour les joueurs virtuels (IA).
 * 
 * <p>Cette classe étend PlayerView pour afficher les messages
 * et informations concernant les joueurs virtuels sans interaction.</p>
 * 
 * <p><b>Affichages :</b></p>
 * <ul>
 *   <li>Messages généraux du jeu</li>
 *   <li>Notifications d'actions des IA</li>
 *   <li>Erreurs de configuration</li>
 * </ul>
 */
public class VirtualView extends PlayerView {
    
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    public void hasNoEnoughCards(String name) {
        System.out.println(name + "doesn't have enough cards to make an offer");
    }

    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        System.out.println("Thank you. You have chosen " + faceUpCard + " as a faceUp card and " + faceDownCard + " as a faceDown card" );
    }

}
