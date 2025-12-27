package model.cards;

import model.players.ScoreVisitorImpl;

public interface CardEffect {
    /**
     * Appelé lors de la phase de recensement (visit).
     * Permet d'activer des "flags" dans le visiteur pour changer les règles.
     */
    default void applyOnVisit(ScoreVisitorImpl visitor) {}

    /**
     * Appelé lors de la phase de calcul final.
     * Permet d'ajouter des points bonus dynamiques.
     */
    default int calculateBonus(ScoreVisitorImpl visitor) { 
        return 0; 
    }
}