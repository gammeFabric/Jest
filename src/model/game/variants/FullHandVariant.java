package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ScoreVisitorImpl;
import model.players.Player;

import java.util.ArrayList;


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
    
    
    public static int calculateCardsPerPlayer(int playerCount, int totalCards) {
        return totalCards / playerCount;
    }
    
    
    public static boolean shouldEndGame(ArrayList<Player> players) {
        for (Player player : players) {
            if (player.getHand().size() > 1) {
                return false;
            }
        }
        return true;
    }
}
