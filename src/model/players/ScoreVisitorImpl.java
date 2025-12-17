package model.players;

import model.cards.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreVisitorImpl implements ScoreVisitor {
    private int totalScore;
    private boolean hasJoker;
    private int heartCount;
    private Map<Suit, List<Face>> suitMap;
    
    // Système de Flags générique (remplace les booleans spécifiques comme hasShield)
    private Map<String, Boolean> flags; 

    public ScoreVisitorImpl() {
        resetScore();
    }

    public void resetScore() {
        totalScore = 0;
        hasJoker = false;
        heartCount = 0;
        suitMap = new HashMap<>();
        flags = new HashMap<>(); // Reset des flags
        for (Suit suit : Suit.values()) {
            suitMap.put(suit, new ArrayList<>());
        }
    }

    // --- Méthodes pour les effets (API publique pour CardEffect) ---
    
    public void setFlag(String flagName, boolean active) {
        flags.put(flagName, active);
    }

    public boolean hasFlag(String flagName) {
        return flags.getOrDefault(flagName, false);
    }
    
    public boolean hasJoker() { return hasJoker; }
    public int getHeartCount() { return heartCount; }
    // -------------------------------------------------------------

    @Override
    public int visit(Card card) {
        if (card instanceof Joker) {
            hasJoker = true;
            return 0;
        } else if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            suitMap.get(suitCard.getSuit()).add(suitCard.getFace());
            if (suitCard.getSuit() == Suit.HEARTS) heartCount++;
            return suitCard.getFaceValue();
        } else if (card instanceof ExtensionCard) {
            // EXTENSIBILITÉ : On délègue la logique à la carte !
            ((ExtensionCard) card).getEffect().applyOnVisit(this);
            return ((ExtensionCard) card).getFaceValue();
        }
        return 0;
    }

    public void countJestScore(Jest jest) {
        resetScore();

        // 1. Recensement (active les flags via applyOnVisit)
        for (Card card : jest.getCards()) {
            visit(card);
        }

        // 2. Calcul standard
        for (Card card : jest.getCards()) {
            if (card instanceof SuitCard) {
                SuitCard sc = (SuitCard) card;
                int effectiveValue = sc.getFaceValue();
                
                if (isSoloAce(sc)) effectiveValue = 5;
                
                applyColorRule(sc, effectiveValue);
            } 
            else if (card instanceof ExtensionCard) {
                // Ajout de la valeur faciale de base
                totalScore += ((ExtensionCard) card).getFaceValue();
                
                // EXTENSIBILITÉ : Calcul des bonus spécifiques
                totalScore += ((ExtensionCard) card).getEffect().calculateBonus(this);
            }
        }

        applyBlackPairBonus();
        applyJokerBonus();
    }

    private boolean isSoloAce(SuitCard sc) {
        if (sc.getFace() == Face.ACE) {
            List<Face> facesOfThisSuit = suitMap.get(sc.getSuit());
            return facesOfThisSuit.size() == 1;
        }
        return false;
    }

    private void applyColorRule(SuitCard sc, int value) {
        Suit suit = sc.getSuit();

        if (suit == Suit.SPADES || suit == Suit.CLUBS) {
            totalScore += value;
        } else if (suit == Suit.DIAMONDS) {
            // Vérification générique : Est-ce qu'un flag interdit les carreaux négatifs ?
            if (!hasFlag("NO_NEGATIVE_DIAMONDS")) { 
                totalScore -= value;
            }
        } else if (suit == Suit.HEARTS) {
            totalScore += calculateIndividualHeartValue(value);
        }
    }
    
    private int calculateIndividualHeartValue(int value) {
        if (!hasJoker) return 0;
        
        if (heartCount == 4) {
            return value;
        } else {
            // Vérification générique : Est-ce qu'un flag interdit les coeurs négatifs ?
            if (hasFlag("NO_NEGATIVE_HEARTS")) return 0;
            return -value;
        }
    }
    
    private void applyBlackPairBonus() {
        List<Face> spades = suitMap.get(Suit.SPADES);
        List<Face> clubs = suitMap.get(Suit.CLUBS);
        for (Face face : spades) {
            if (clubs.contains(face)) totalScore += 2;
        }
    }

    private void applyJokerBonus() {
        if (hasJoker && heartCount == 0) totalScore += 4;
    }
    
    public int getTotalScore() { return totalScore; }
}