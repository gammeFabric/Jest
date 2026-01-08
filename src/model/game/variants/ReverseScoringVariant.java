package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ReverseScoreVisitor;

/**
 * Variant that reverses scoring: positive totals become negative
 * and negative totals become positive, while using the same
 * underlying card scoring rules as the standard variant.
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
        // No special setup; only scoring is changed via the visitor.
    }

    @Override
    public String getRulesDescription() {
        return DESCRIPTION;
    }

    @Override
    public ScoreVisitor createScoreVisitor() {
        // Use the reverse scoring visitor
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