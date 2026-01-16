package model.players;

import model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Représente la collection de cartes d'un joueur.
 * 
 * <p>Le Jest accumule toutes les cartes choisies par un joueur durant
 * la partie. C'est sur cette collection qu'est calculé le score final.</p>
 * 
 * <p><b>Fonctionnalités :</b></p>
 * <ul>
 *   <li>Ajout de cartes au fil du jeu</li>
 *   <li>Acceptation d'un visiteur de score</li>
 *   <li>Conservation de l'ordre d'acquisition</li>
 * </ul>
 * 
 * <p><b>Pattern Visitor :</b></p>
 * <pre>
 * jest.accept(scoreVisitor);  // Parcourt toutes les cartes
 * int score = scoreVisitor.getTotalScore();
 * </pre>
 * 
 * <p><b>Sérialisable</b> pour la sauvegarde de parties.</p>
 * 
 * @see model.players.ScoreVisitor
 * @see model.players.Player
 */
public class Jest implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Card> cards;

    public Jest() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void accept(ScoreVisitor visitor) {
        for (Card card : cards) {
            visitor.visit(card);
        }
        if (visitor instanceof ScoreVisitorImpl) {
            ((ScoreVisitorImpl) visitor).countJestScore(this);
        }
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
