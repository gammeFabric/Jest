package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ScoreVisitorImpl;
import model.players.Player;

import java.util.ArrayList;

/**
 * Full Hand variant of the Jest card game.
 * In this variant, all cards are distributed fairly to each player at the beginning.
 * Each turn, players choose two cards from their entire hand to make offers.
 * The game continues until each player has only one card left, which becomes part of their Jest.
 */
public class FullHandVariant implements GameVariant {
    
    private static final String NAME = "Full Hand";
    private static final String DESCRIPTION = 
        "Full Hand variant rules:\n" +
        "- All cards are distributed fairly at the beginning\n" +
        "- Each turn, players choose 2 cards from their entire hand\n" +
        "- Offers are made following classic rules\n" +
        "- Game continues until each player has only 1 card left\n" +
        "- The last card of each player automatically joins their Jest\n" +
        "- Uses standard Jest scoring rules";
    
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setup(Game game) {
        // This variant requires special setup for full card distribution
        // The actual distribution will be handled by a custom round controller
        // or by modifying the game flow to use FullHandRound instead of regular Round
    }

    @Override
    public String getRulesDescription() {
        return DESCRIPTION;
    }

    @Override
    public ScoreVisitor createScoreVisitor() {
        // Use standard scoring rules
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
    
    /**
     * Calculates how many cards each player should receive at the start.
     * @param playerCount number of players
     * @param totalCards total number of cards in deck
     * @return number of cards per player
     */
    public static int calculateCardsPerPlayer(int playerCount, int totalCards) {
        return totalCards / playerCount;
    }
    
    /**
     * Checks if the game should end based on remaining cards.
     * In this variant, the game ends when each player has only one card left.
     * @param players list of players
     * @return true if game should end
     */
    public static boolean shouldEndGame(ArrayList<Player> players) {
        for (Player player : players) {
            if (player.getHand().size() > 1) {
                return false;
            }
        }
        return true;
    }
}
