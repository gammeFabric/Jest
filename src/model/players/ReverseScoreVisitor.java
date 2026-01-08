package model.players;

/**
 * ReverseScoreVisitor uses the standard scoring rules from {@link ScoreVisitorImpl}
 * but inverts the final score (positives become negatives and negatives become positives).
 *
 * This is used by variants that want a \"reverse scoring\" effect without
 * duplicating the underlying scoring logic.
 */
public class ReverseScoreVisitor extends ScoreVisitorImpl {

    @Override
    public int getTotalScore() {
        // Negate the score computed by ScoreVisitorImpl
        return -super.getTotalScore();
    }
}