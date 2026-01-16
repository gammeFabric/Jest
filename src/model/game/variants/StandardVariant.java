package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ScoreVisitorImpl;

/**
 * Variante standard - Règles classiques du Jest.
 * 
 * <p>Cette variante implémente les règles originales du jeu Jest
 * sans modification.</p>
 * 
 * <p><b>Règles de score :</b></p>
 * <ul>
 *   <li><b>Trèfle/Pique</b> : +valeur de la carte</li>
 *   <li><b>Carreau</b> : -valeur de la carte</li>
 *   <li><b>Cœur</b> :
 *     <ul>
 *       <li>0 points sans Joker</li>
 *       <li>+valeur si Joker + 4 Cœurs</li>
 *       <li>-valeur si Joker + 1-3 Cœurs</li>
 *     </ul>
 *   </li>
 *   <li><b>As solitaire</b> : 5 points au lieu de 1</li>
 *   <li><b>Paire noire</b> : +2 par carte (même valeur en Trèfle et Pique)</li>
 *   <li><b>Joker seul</b> : +4 points (sans Cœur)</li>
 * </ul>
 * 
 * <p><b>Déroulement :</b></p>
 * <ul>
 *   <li>Tours successifs de 2 cartes par joueur</li>
 *   <li>Offres avec 1 carte visible, 1 cachée</li>
 *   <li>Sélection séquentielle des cartes</li>
 * </ul>
 * 
 * <p><b>Joueurs :</b> 3-4 joueurs</p>
 * 
 * @see model.players.ScoreVisitorImpl
 * @see model.game.GameVariant
 */
public class StandardVariant implements GameVariant {

    private static final String NAME = "Standard";
    private static final String DESCRIPTION = "The classic Jest card game rules:\n" +
            "- Black cards (Spades, Clubs) add their face value\n" +
            "- Diamonds subtract their face value\n" +
            "- Hearts: 0 points without Joker, +value if all 4 hearts with Joker, -value otherwise\n" +
            "- Solo Ace (only Ace of a suit) counts as 5 points\n" +
            "- Black pairs (same face in Spades and Clubs) add +2 points each\n" +
            "- Joker with no hearts adds +4 points";

    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setup(Game game) {

    }

    @Override
    public String getRulesDescription() {
        return DESCRIPTION;
    }

    @Override
    public ScoreVisitor createScoreVisitor() {

        return new ScoreVisitorImpl();
    }

    @Override
    public int getMinPlayers() {
        return MIN_PLAYERS;
    }

    @Override
    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }
}
