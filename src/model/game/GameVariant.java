package model.game;

import model.players.ScoreVisitor;
import java.io.Serializable;

public interface GameVariant extends Serializable {
    /**
     * Returns the name of the game variant.
     * @return the variant name
     */
    String getName();

    /**
     * Sets up the initial state of the game for this variant.
     * This method is called when the variant is selected to configure
     * any variant-specific game rules or state.
     * @param game the game instance to configure
     */
    void setup(Game game);

    /**
     * Returns a description of the rules specific to this variant.
     * @return rules description
     */
    String getRulesDescription();

    /**
     * Creates and returns a ScoreVisitor instance for this variant.
     * Each variant can provide its own scoring implementation.
     * @return a ScoreVisitor implementation for this variant
     */
    ScoreVisitor createScoreVisitor();

    /**
     * Returns the minimum number of players required for this variant.
     * @return minimum number of players (default: 3)
     */
    int getMinPlayers();

    /**
     * Returns the maximum number of players allowed for this variant.
     * @return maximum number of players (default: 4)
     */
    int getMaxPlayers();
}
