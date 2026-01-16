package model.players.strategies;

import model.cards.Card;
import model.players.Jest;
import model.players.Offer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Interface définissant une stratégie de jeu pour les joueurs virtuels.
 * 
 * <p>Cette interface permet d'implémenter différentes stratégies de décision
 * pour les IA, indépendamment de leur logique spécifique.</p>
 * 
 * <p><b>Responsabilités :</b></p>
 * <ul>
 *   <li>Sélection de 2 cartes pour former une offre</li>
 *   <li>Choix de l'offre adverse à accepter</li>
 *   <li>Mise à jour de la connaissance du Jest du joueur</li>
 * </ul>
 * 
 * @see AggressiveStrategy
 * @see CautiousStrategy
 * @see RandomStrategy
 */
public interface PlayStrategy extends Serializable {
    Card[] setCardsToOffer(ArrayList<Card> hand);
    Offer chooseCard(ArrayList<Offer> availableOffers);
    void updateJest(Jest jest);
}
