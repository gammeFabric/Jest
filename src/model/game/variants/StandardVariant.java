package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ScoreVisitorImpl;

/**
 * Standard variant of the Jest card game.
 * This variant uses the classic Jest scoring rules and supports 3-4 players.
 */
public class StandardVariant implements GameVariant {
    
    private static final String NAME = "Standard";
    private static final String DESCRIPTION = 
        "The classic Jest card game rules:\n" +
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
        // Standard variant doesn't require any special setup
        // The game uses default rules and deck configuration
    }

    @Override
    public String getRulesDescription() {
        return DESCRIPTION;
    }

    @Override
    public ScoreVisitor createScoreVisitor() {
        // Return the standard scoring implementation
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

