package model.players;

import model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Représente une offre de deux cartes.
 * 
 * <p>Chaque tour, les joueurs créent une offre composée d'une carte
 * visible (face up) et d'une carte cachée (face down).</p>
 * 
 * <p><b>États d'une offre :</b></p>
 * <ul>
 *   <li><b>Complète</b> : Les deux cartes sont présentes</li>
 *   <li><b>Partielle</b> : Une carte a été prise</li>
 *   <li><b>Vide</b> : Les deux cartes ont été prises</li>
 * </ul>
 * 
 * <p><b>Méthodes principales :</b></p>
 * <ul>
 *   <li><code>takeCard(boolean)</code> - Retire et retourne une carte (true=visible)</li>
 *   <li><code>isComplete()</code> - Vérifie si les 2 cartes sont présentes</li>
 *   <li><code>getOfferedCard()</code> - Liste les cartes encore présentes</li>
 * </ul>
 * 
 * <p><b>Propriétaire :</b> Référence au joueur ayant créé l'offre.</p>
 * 
 * @see model.players.Player
 * @see model.game.Round
 */
public class Offer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Player owner;
    private Card faceUpCard;
    private Card faceDownCard;

    public Offer(Player owner, Card faceUpCard, Card faceDownCard) {
        this.owner = owner;
        this.faceUpCard = faceUpCard;
        this.faceDownCard = faceDownCard;
    }

    public Player getOwner() {
        return owner;
    }

    public Card takeCard(boolean takeFaceUp) {
        if (takeFaceUp) {
            Card takenCard = faceUpCard;
            faceUpCard = null;
            return takenCard;
        } else {
            Card takenCard = faceDownCard;
            faceDownCard = null;
            return takenCard;
        }
    }

    public boolean isComplete() {
        return faceUpCard != null && faceDownCard != null;
    }

    public Card getFaceUpCard() {
        return faceUpCard;
    }

    public Card getFaceDownCard() {
        return faceDownCard;
    }

    public void setFaceUpCard(Card faceUpCard) {
        this.faceUpCard = faceUpCard;
    }

    public void setFaceDownCard(Card faceDownCard) {
        this.faceDownCard = faceDownCard;
    }

    public ArrayList<Card> getOfferedCard() {
        ArrayList<Card> cards = new ArrayList<>();

        if (faceUpCard != null)
            cards.add(faceUpCard);
        if (faceDownCard != null)
            cards.add(faceDownCard);
        return cards;
    }

    @Override
    public String toString() {
        return faceUpCard + " and one hidden card";
    }
}
