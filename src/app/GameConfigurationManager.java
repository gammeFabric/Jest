package app;

import model.game.GameConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages saving and loading game configurations for restart functionality.
 */
public class GameConfigurationManager {
    private static final String CONFIG_FILE = "game_config.ser";
    
    /**
     * Saves the game configuration to a file.
     * @param configuration the game configuration to save
     */
    public static void saveConfiguration(GameConfiguration configuration) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(configuration);
        } catch (IOException e) {
            System.err.println("Error saving game configuration: " + e.getMessage());
        }
    }
    
    /**
     * Loads the game configuration from file.
     * @return the loaded configuration, or null if not found or error
     */
    public static GameConfiguration loadConfiguration() {
        Path configPath = Paths.get(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(CONFIG_FILE))) {
            return (GameConfiguration) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game configuration: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Deletes the saved configuration file.
     */
    public static void deleteConfiguration() {
        try {
            Files.deleteIfExists(Paths.get(CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("Error deleting game configuration: " + e.getMessage());
        }
    }
}
