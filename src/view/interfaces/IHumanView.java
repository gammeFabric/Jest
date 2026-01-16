package view.interfaces;

import model.cards.Card;
import model.players.Offer;

import java.util.ArrayList;

/**
 * Interface pour les interactions avec un joueur humain.
 * 
 * <p>Cette interface définit les méthodes pour demander les décisions
 * d'un joueur humain lors d'une partie de Jest.</p>
 * 
 * <p><b>Décisions gérées :</b></p>
 * <ul>
 *   <li>Sélection de cartes pour créer une offre</li>
 *   <li>Choix de la carte face visible</li>
 *   <li>Sélection d'une offre adverse</li>
 *   <li>Choix entre carte face visible ou cachée</li>
 * </ul>
 * 
 * @see view.console.HumanView
 * @see view.gui.HumanViewGUI
 * @see view.hybrid.HumanViewHybrid
 */
public interface IHumanView extends IPlayerView {
    int chooseFaceUpCard(String playerName, ArrayList<Card> hand);
    Offer chooseOffer(String playerName, ArrayList<Offer> selectableOffers);
    boolean chooseFaceUpOrDown();
    void thankForChoosing(Card faceUpCard, Card faceDownCard);
    
    
    int[] chooseTwoCardsForOffer(String playerName, ArrayList<Card> hand);
}

