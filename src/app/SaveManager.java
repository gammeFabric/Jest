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

/**
 * Gestionnaire de sauvegarde et chargement de parties.
 * 
 * <p>Cette classe gère la persistance complète de l'état d'une partie
 * en cours, permettant de sauvegarder et reprendre une partie à tout moment.</p>
 * 
 * <p><b>Fonctionnalités :</b></p>
 * <ul>
 *   <li>Sauvegarde sérialisée de l'état complet du jeu</li>
 *   <li>Chargement et restauration d'une partie sauvegardée</li>
 *   <li>Listage des sauvegardes disponibles</li>
 *   <li>Gestion automatique du répertoire de sauvegardes</li>
 * </ul>
 * 
 * <p><b>Format de sauvegarde :</b></p>
 * <ul>
 *   <li>Répertoire : <code>./saves/</code></li>
 *   <li>Extension : <code>.jest</code></li>
 *   <li>Nommage : <code>game_[nom]_[timestamp].jest</code></li>
 * </ul>
 * 
 * <p><b>Exemple d'utilisation :</b></p>
 * <pre>
 * // Sauvegarde
 * SaveManager.save(game, "ma_partie");
 * 
 * // Liste des sauvegardes
 * List&lt;String&gt; saves = SaveManager.listSaves();
 * 
 * // Chargement
 * Game loadedGame = SaveManager.load("game_ma_partie_2025-01-16.jest");
 * </pre>
 * 
 * @see model.game.Game
 */
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

    /**
     * Liste les fichiers de sauvegarde disponibles dans le répertoire de sauvegardes.
     *
     * <p>Les noms retournés sont des noms de fichiers (sans chemin). La liste est
     * triée par ordre lexicographique.</p>
     *
     * @return liste des noms de fichiers de sauvegarde (éventuellement vide)
     */
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

    /**
     * Sauvegarde une partie sur disque en sérialisant l'objet {@link Game}.
     *
     * <p>Le compteur de tours ({@link Round#getRoundCounter()}) est stocké dans le modèle
     * avant la sérialisation afin de pouvoir être restauré lors du chargement.</p>
     *
     * @param game partie à sauvegarder
     * @param saveName nom logique de sauvegarde (peut être vide) ; sera normalisé et préfixé
     * @throws IllegalArgumentException si {@code game} est {@code null}
     * @throws RuntimeException si l'écriture échoue
     */
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

    /**
     * Charge une partie depuis un fichier de sauvegarde.
     *
     * <p>Après désérialisation, le compteur de tours statique de {@link Round} est
     * réinitialisé à la valeur stockée dans la sauvegarde.</p>
     *
     * @param fileName nom du fichier de sauvegarde (relatif au répertoire {@code saves})
     * @return partie chargée
     * @throws IllegalArgumentException si {@code fileName} est vide
     * @throws RuntimeException si la lecture échoue ou si le fichier ne contient pas un {@link Game}
     */
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
