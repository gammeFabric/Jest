package app;

import model.game.Game;
import model.game.Round;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SaveManager {
    private static final String SAVES_DIR_NAME = "saves";
    private static final String EXTENSION = ".jest";

    private static Path ensureSavesDir() {
        Path dir = Paths.get(SAVES_DIR_NAME);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create saves directory: " + dir.toAbsolutePath(), e);
        }
        return dir;
    }

    public static List<String> listSaves() {
        Path dir = ensureSavesDir();
        File[] files = dir.toFile().listFiles((d, name) -> name.toLowerCase().endsWith(EXTENSION));
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        ArrayList<String> result = new ArrayList<>();
        for (File f : files) {
            result.add(f.getName());
        }
        return result;
    }

    public static void save(Game game, String saveName) {
        if (game == null) {
            throw new IllegalArgumentException("game must not be null");
        }

        game.setSavedRoundCounter(Round.getRoundCounter());

        String baseName = buildBaseName(saveName);
        String fileName = baseName.endsWith(EXTENSION) ? baseName : baseName + EXTENSION;

        Path dir = ensureSavesDir();
        Path path = dir.resolve(fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path.toFile())))) {
            oos.writeObject(game);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game to " + path.toAbsolutePath(), e);
        }
    }

    public static Game load(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("fileName must not be empty");
        }

        Path dir = ensureSavesDir();
        Path path = dir.resolve(fileName);

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toFile())))) {
            Object obj = ois.readObject();
            Game game = (Game) obj;

            Round.setRoundCounter(game.getSavedRoundCounter());
            return game;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load game from " + path.toAbsolutePath(), e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Save file does not contain a Game: " + path.toAbsolutePath(), e);
        }
    }

    private static String buildBaseName(String userProvided) {
        String suffix;
        if (userProvided == null || userProvided.trim().isEmpty()) {
            suffix = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        } else {
            suffix = userProvided.trim();
        }

        suffix = suffix.replaceAll("[^a-zA-Z0-9_-]", "_");
        return "game_" + suffix;
    }
}
