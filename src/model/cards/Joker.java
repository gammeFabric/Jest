package model.cards;

/**
 * Représente la carte Joker.
 * 
 * <p>Le Joker est une carte spéciale avec des règles de score complexes
 * selon les cartes Cœur collectées.</p>
 * 
 * <p><b>Règles de score :</b></p>
 * <ul>
 *   <li>Sans Cœur : +4 points</li>
 *   <li>Avec les 4 Cœurs : valeurs positives des Cœurs</li>
 *   <li>Avec 1-3 Cœurs : valeurs négatives des Cœurs</li>
 * </ul>
 * 
 * <p>Le Joker n'a pas de valeur nominale ou de couleur.</p>
 * 
 * @see model.cards.Card
 * @see model.players.ScoreVisitorImpl
 */
public class Joker extends Card {
    public Joker(boolean isTrophy) {
        super(isTrophy);
    }

    @Override
    public int getFaceValue() {
        return 0;
    }

    @Override
    public int getSuitValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "Joker";
    }
}
