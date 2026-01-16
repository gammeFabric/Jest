package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ReverseScoreVisitor;

/**
 * Variante "Reverse Scoring" - Scores inversés.
 * 
 * <p>Cette variante inverse tous les scores finaux :
 * les totaux positifs deviennent négatifs et vice-versa.</p>
 * 
 * <p><b>Modifications :</b></p>
 * <ul>
 *   <li>Calcul des scores identique aux règles standard</li>
 *   <li>Inversion du score total en fin de partie</li>
 *   <li>Les cartes négatives deviennent donc avantageuses</li>
 *   <li>Les cartes positives deviennent pénalisantes</li>
 * </ul>
 * 
 * <p><b>Impact stratégique :</b></p>
 * <ul>
 *   <li>Carreaux deviennent désirables (+valeur après inversion)</li>
 *   <li>Trèfles/Piques deviennent pénalisants (-valeur après inversion)</li>
 *   <li>Le Joker reste complexe selon les Cœurs collectés</li>
 * </ul>
 * 
 * <p><b>Implémentation :</b> Utilise {@link model.players.ReverseScoreVisitor}</p>
 * 
 * <p><b>Joueurs :</b> 3-4 joueurs</p>
 * 
 * @see model.players.ReverseScoreVisitor
 * @see model.game.GameVariant
 */
public class ReverseScoringVariant implements GameVariant {

    private static final String NAME = "Reverse Scoring";
    private static final String DESCRIPTION =
            "All final Jest scores are inverted:\n" +
            "- Positive totals become negative\n" +
            "- Negative totals become positive\n" +
            "Card interactions and trophies are evaluated as in the standard rules.";

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
        
        return new ReverseScoreVisitor();
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