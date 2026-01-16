package model.cards;

import model.players.ScoreVisitorImpl;

public interface CardEffect {
    
    default void applyOnVisit(ScoreVisitorImpl visitor) {}

    
    default int calculateBonus(ScoreVisitorImpl visitor) { 
        return 0; 
    }
}