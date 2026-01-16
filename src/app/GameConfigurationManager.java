package app;

import model.game.GameConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class GameConfigurationManager {
    private static final String CONFIG_FILE = "game_config.ser";
    
    
    public static void saveConfiguration(GameConfiguration configuration) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CONFIG_FILE))) {
            oos.writeObject(configuration);
        } catch (IOException e) {
            System.err.println("Error saving game configuration: " + e.getMessage());
        }
    }
    
    
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
    
    
    public static void deleteConfiguration() {
        try {
            Files.deleteIfExists(Paths.get(CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("Error deleting game configuration: " + e.getMessage());
        }
    }
}
