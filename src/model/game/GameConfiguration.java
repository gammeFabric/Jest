package model.game;

import model.cards.ExtensionCard;
import model.players.Player;
import model.players.strategies.StrategyType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores the complete configuration of a game to enable restart functionality.
 * This includes player information, variant, extensions, and all game parameters.
 */
public class GameConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final ArrayList<PlayerConfiguration> playerConfigs;
    private final GameVariant variant;
    private final ArrayList<ExtensionCard> selectedExtensions;
    private final int playerCount;
    
    public GameConfiguration(ArrayList<Player> players, GameVariant variant, 
                           ArrayList<ExtensionCard> selectedExtensions) {
        this.playerConfigs = new ArrayList<>();
        this.variant = variant;
        this.selectedExtensions = new ArrayList<>(selectedExtensions);
        this.playerCount = players.size();
        
        // Extract player configurations
        for (Player player : players) {
            boolean isHuman = player instanceof model.players.HumanPlayer;
            StrategyType strategy = null;
            
            if (!isHuman && player instanceof model.players.VirtualPlayer) {
                strategy = ((model.players.VirtualPlayer) player).getStrategy();
            }
            
            playerConfigs.add(new PlayerConfiguration(player.getName(), isHuman, strategy));
        }
    }
    
    public ArrayList<PlayerConfiguration> getPlayerConfigs() {
        return playerConfigs;
    }
    
    public GameVariant getVariant() {
        return variant;
    }
    
    public ArrayList<ExtensionCard> getSelectedExtensions() {
        return selectedExtensions;
    }
    
    public int getPlayerCount() {
        return playerCount;
    }
    
    /**
     * Configuration for a single player
     */
    public static class PlayerConfiguration implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final String name;
        private final boolean isHuman;
        private final StrategyType strategy;
        
        public PlayerConfiguration(String name, boolean isHuman, StrategyType strategy) {
            this.name = name;
            this.isHuman = isHuman;
            this.strategy = strategy;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isHuman() {
            return isHuman;
        }
        
        public StrategyType getStrategy() {
            return strategy;
        }
    }
}
