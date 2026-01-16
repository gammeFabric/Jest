package app;

import controller.GameController;
import model.game.Game;
import model.game.GameConfiguration;
import view.ViewFactory;
import view.console.GameView;
import view.console.RoundView;
import view.gui.GameViewGUI;
import view.gui.GameWindow;
import view.gui.RoundViewGUI;
import view.hybrid.GameViewHybrid;
import view.hybrid.RoundViewHybrid;
import view.interfaces.IGameView;
import view.interfaces.IRoundView;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.*;

public class GameLauncher {
    public enum GameMode {
        CONSOLE, GUI, HYBRID
    }

    
    private static GameMode currentMode;
    private static GameWindow currentGameWindow;

    public static void main(String[] args) {
        boolean isRestart = args.length > 0 && "--restart".equals(args[0]);
        GameMode mode = selectMode();
        currentMode = mode; 
        
        System.out.println("--- Jest Card Game ---");
        if (mode == GameMode.GUI || mode == GameMode.HYBRID) {
            System.out.println("Starting in " + mode + " mode..." + (isRestart ? " (Restart)" : ""));
        }

        IGameView gameView;
        IRoundView roundView;
        ViewFactory viewFactory;
        GameWindow gameWindow = null;

        switch (mode) {
            case CONSOLE:
                gameView = new GameView();
                roundView = new RoundView();
                viewFactory = new ViewFactory(ViewFactory.ViewMode.CONSOLE);
                break;
            case GUI:
                gameWindow = new GameWindow();
                currentGameWindow = gameWindow; 
                gameWindow.show();
                gameView = new GameViewGUI(gameWindow.getFrame(), gameWindow.getOutputArea(), gameWindow.getCardPanel(), gameWindow);
                roundView = new RoundViewGUI(gameWindow.getOutputArea(), gameWindow.getOffersPanel(), gameWindow.getHandPanel(),
                                            gameWindow.getBotAreasPanel(), gameWindow.getDeckPanel(), gameWindow.getTrophiesPanel());
                viewFactory = new ViewFactory(ViewFactory.ViewMode.GUI, 
                                             gameWindow.getOutputArea(), 
                                             gameWindow.getCardPanel(),
                                             gameWindow.getHandPanel(),
                                             gameWindow.getOffersPanel(),
                                             gameWindow.getInteractionPanel());
                break;
            case HYBRID:
                gameWindow = new GameWindow();
                currentGameWindow = gameWindow; 
                gameWindow.show();
                GameView consoleGameView = new GameView();
                GameViewGUI guiGameView = new GameViewGUI(gameWindow.getFrame(), gameWindow.getOutputArea(), gameWindow.getCardPanel(), gameWindow);
                gameView = new GameViewHybrid(consoleGameView, guiGameView);
                RoundView consoleRoundView = new RoundView();
                RoundViewGUI guiRoundView = new RoundViewGUI(gameWindow.getOutputArea(), gameWindow.getOffersPanel(), gameWindow.getHandPanel(),
                                                            gameWindow.getBotAreasPanel(), gameWindow.getDeckPanel(), gameWindow.getTrophiesPanel(), true);
                roundView = new RoundViewHybrid(consoleRoundView, guiRoundView);
                viewFactory = new ViewFactory(ViewFactory.ViewMode.HYBRID, 
                                             gameWindow.getOutputArea(), 
                                             gameWindow.getCardPanel(),
                                             gameWindow.getHandPanel(),
                                             gameWindow.getOffersPanel(),
                                             gameWindow.getInteractionPanel());
                break;
            default:
                gameView = new GameView();
                roundView = new RoundView();
                viewFactory = new ViewFactory(ViewFactory.ViewMode.CONSOLE);
        }

        Game model = selectNewOrLoadGame(mode, gameWindow, isRestart);

        GameController controller = new GameController(model, gameView, roundView, viewFactory);
        controller.startGame();
        
        
        if (!isRestart) {
            GameConfigurationManager.deleteConfiguration();
        }
    }

    
    private static Game createGameFromConfiguration(GameConfiguration config) {
        Game game = new Game();
        
        
        game.setVariant(config.getVariant());
        
        
        for (GameConfiguration.PlayerConfiguration playerConfig : config.getPlayerConfigs()) {
            if (playerConfig.isHuman()) {
                game.addHumanPlayer(playerConfig.getName());
            } else {
                game.addVirtualPlayer(playerConfig.getName(), playerConfig.getStrategy());
            }
        }
        
        
        if (!config.getSelectedExtensions().isEmpty()) {
            game.getDeck().addExtensions(config.getSelectedExtensions());
        }
        
        
        game.chooseTrophies(config.getPlayerCount());
        
        return game;
    }

    private static Game selectNewOrLoadGame(GameMode mode, GameWindow gameWindow, boolean isRestart) {
        if (isRestart) {
            
            GameConfiguration config = GameConfigurationManager.loadConfiguration();
            if (config != null) {
                return createGameFromConfiguration(config);
            } else {
                
                System.out.println("No saved configuration found. Starting new game.");
                return new Game();
            }
        }
        
        if (mode == GameMode.GUI) {
            String[] options = {"New Game", "Load Game"};
            int choice = JOptionPane.showOptionDialog(
                    gameWindow != null ? gameWindow.getFrame() : null,
                    "Start a new game or load a saved game?",
                    "Jest Card Game",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice == 1) {
                return loadGameGui(gameWindow);
            }
            return new Game();
        }

        if (mode == GameMode.HYBRID) {
            if (isRestart) {
                
                GameConfiguration config = GameConfigurationManager.loadConfiguration();
                if (config != null) {
                    return createGameFromConfiguration(config);
                } else {
                    
                    System.out.println("No saved configuration found. Starting new game.");
                    return new Game();
                }
            }
            
            
            final class Choice<T> {
                private final Object lock = new Object();
                private boolean resolved;
                private T value;

                boolean isResolved() {
                    synchronized (lock) {
                        return resolved;
                    }
                }

                void resolve(T value) {
                    synchronized (lock) {
                        if (resolved) {
                            return;
                        }
                        resolved = true;
                        this.value = value;
                        lock.notifyAll();
                    }
                }

                T await() {
                    synchronized (lock) {
                        while (!resolved) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return value;
                            }
                        }
                        return value;
                    }
                }
            }

            Choice<Integer> startChoice = new Choice<>();

            final class DialogRef {
                private volatile JDialog dialog;
            }
            DialogRef guiDialogRef = new DialogRef();

            Thread guiThread = new Thread(() -> {
                String[] options = {"New Game", "Load Game"};
                JOptionPane pane = new JOptionPane(
                        "Start a new game or load a saved game?",
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.DEFAULT_OPTION,
                        null,
                        options,
                        options[0]
                );

                JDialog dialog = pane.createDialog(gameWindow != null ? gameWindow.getFrame() : null, "Jest Card Game");
                guiDialogRef.dialog = dialog;
                dialog.setVisible(true);

                Object selected = pane.getValue();
                guiDialogRef.dialog = null;
                if (selected == null) {
                    startChoice.resolve(0);
                    return;
                }
                for (int i = 0; i < options.length; i++) {
                    if (options[i].equals(selected)) {
                        startChoice.resolve(i);
                        return;
                    }
                }
                startChoice.resolve(0);
            }, "Hybrid-GUI-SelectNewOrLoad");
            guiThread.setDaemon(true);
            guiThread.start();

            Thread consoleThread = new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("1. Start new game");
                System.out.println("2. Load saved game");

                while (!startChoice.isResolved()) {
                    try {
                        if (reader.ready()) {
                            System.out.print("Enter choice (1-2): ");
                            String line = reader.readLine();
                            int choice;
                            try {
                                choice = Integer.parseInt(line.trim());
                            } catch (NumberFormatException e) {
                                choice = 1;
                            }
                            startChoice.resolve(choice == 2 ? 1 : 0);
                            JDialog d = guiDialogRef.dialog;
                            if (d != null) {
                                SwingUtilities.invokeLater(() -> {
                                    d.setVisible(false);
                                    d.dispose();
                                });
                                guiDialogRef.dialog = null;
                            }
                            return;
                        }
                    } catch (IOException e) {
                        startChoice.resolve(0);
                        return;
                    }

                    synchronized (startChoice.lock) {
                        if (startChoice.isResolved()) {
                            return;
                        }
                        try {
                            startChoice.lock.wait(75);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            startChoice.resolve(0);
                            return;
                        }
                    }
                }
            }, "Hybrid-Console-SelectNewOrLoad");
            consoleThread.setDaemon(true);
            consoleThread.start();

            Integer resolved = startChoice.await();
            if (resolved != null && resolved == 1) {
                
                return loadGameGui(gameWindow);
            }
            return new Game();
        }

        
        if (isRestart) {
            
            GameConfiguration config = GameConfigurationManager.loadConfiguration();
            if (config != null) {
                return createGameFromConfiguration(config);
            } else {
                
                System.out.println("No saved configuration found. Starting new game.");
                return new Game();
            }
        }
        
        
        int choice;
        try {
            System.out.println("1. Start new game");
            System.out.println("2. Load saved game");
            System.out.print("Enter choice (1-2): ");

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String input = consoleReader.readLine();
            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                choice = 1;
            }
        } catch (IOException e1) {
            choice = 1;
        }

        if (choice != 2) {
            return new Game();
        }

        List<String> saves = SaveManager.listSaves();
        if (saves.isEmpty()) {
            System.out.println("No saves found in /saves. Starting a new game.");
            return new Game();
        }

        System.out.println("Available saves:");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }
        System.out.print("Select save (1-" + saves.size() + "): ");

        int idx;
        try {
            BufferedReader saveReader = new BufferedReader(new InputStreamReader(System.in));
            String saveInput = saveReader.readLine();
            try {
                idx = Integer.parseInt(saveInput.trim());
            } catch (NumberFormatException e) {
                idx = 1;
            }
        } catch (IOException e2) {
            idx = 1;
        }
        if (idx < 1 || idx > saves.size()) {
            System.out.println("Invalid choice. Starting a new game.");
            return new Game();
        }

        return SaveManager.load(saves.get(idx - 1));
    }

    private static Game loadGameGui(GameWindow gameWindow) {
        List<String> saves = SaveManager.listSaves();
        if (saves.isEmpty()) {
            JOptionPane.showMessageDialog(
                    gameWindow != null ? gameWindow.getFrame() : null,
                    "No saves found in /saves. Starting a new game.",
                    "Load Game",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return new Game();
        }

        String selected = (String) JOptionPane.showInputDialog(
                gameWindow != null ? gameWindow.getFrame() : null,
                "Select a save to load:",
                "Load Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                saves.toArray(new String[0]),
                saves.getFirst()
        );

        if (selected == null || selected.trim().isEmpty()) {
            return new Game();
        }
        return SaveManager.load(selected);
    }

    private static GameMode selectMode() {
        if (System.console() == null) {
            
            String[] options = {"Console", "GUI", "Hybrid"};
            int choice = JOptionPane.showOptionDialog(
                null,
                "Select game mode:",
                "Jest Card Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            return switch (choice) {
                case 0 -> GameMode.CONSOLE;
                case 1 -> GameMode.GUI;
                case 2 -> GameMode.HYBRID;
                default -> GameMode.CONSOLE;
            };
        } else {
            
            try {
                System.out.println("Select game mode:");
                System.out.println("1. Console");
                System.out.println("2. GUI");
                System.out.println("3. Hybrid (Console + GUI)");
                System.out.print("Enter choice (1-3): ");

                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String input = consoleReader.readLine();
                if (input == null) input = "1";
                input = input.trim().toLowerCase();

                return switch (input) {
                    case "1", "console" -> GameMode.CONSOLE;
                    case "2", "gui" -> GameMode.GUI;
                    case "3", "hybrid" -> GameMode.HYBRID;
                    default -> {
                        System.out.println("Invalid choice, defaulting to Console mode");
                        yield GameMode.CONSOLE;
                    }
                };
            } catch (IOException e3) {
                return GameMode.CONSOLE;
            }
        }
    }

    
    public static void restartGame() {
        if (currentMode == null) {
            
            main(new String[]{"--restart"});
            return;
        }

        
        GameConfiguration config = GameConfigurationManager.loadConfiguration();
        Game model;
        if (config != null) {
            model = createGameFromConfiguration(config);
        } else {
            System.out.println("No saved configuration found. Starting new game.");
            model = new Game();
        }

        
        IGameView gameView;
        IRoundView roundView;
        ViewFactory viewFactory;

        switch (currentMode) {
            case CONSOLE:
                gameView = new GameView();
                roundView = new RoundView();
                viewFactory = new ViewFactory(ViewFactory.ViewMode.CONSOLE);
                break;
            case GUI:
                
                GameWindow gameWindow = currentGameWindow;
                gameView = new GameViewGUI(gameWindow.getFrame(), gameWindow.getOutputArea(), gameWindow.getCardPanel(), gameWindow);
                roundView = new RoundViewGUI(gameWindow.getOutputArea(), gameWindow.getOffersPanel(), gameWindow.getHandPanel(),
                                            gameWindow.getBotAreasPanel(), gameWindow.getDeckPanel(), gameWindow.getTrophiesPanel());
                viewFactory = new ViewFactory(ViewFactory.ViewMode.GUI, 
                                             gameWindow.getOutputArea(), 
                                             gameWindow.getCardPanel(),
                                             gameWindow.getHandPanel(),
                                             gameWindow.getOffersPanel(),
                                             gameWindow.getInteractionPanel());
                break;
            case HYBRID:
                
                GameWindow hybridWindow = currentGameWindow;
                GameView consoleGameView = new GameView();
                GameViewGUI guiGameView = new GameViewGUI(hybridWindow.getFrame(), hybridWindow.getOutputArea(), hybridWindow.getCardPanel(), hybridWindow);
                gameView = new GameViewHybrid(consoleGameView, guiGameView);
                RoundView consoleRoundView = new RoundView();
                RoundViewGUI guiRoundView = new RoundViewGUI(hybridWindow.getOutputArea(), hybridWindow.getOffersPanel(), hybridWindow.getHandPanel(),
                                                            hybridWindow.getBotAreasPanel(), hybridWindow.getDeckPanel(), hybridWindow.getTrophiesPanel(), true);
                roundView = new RoundViewHybrid(consoleRoundView, guiRoundView);
                viewFactory = new ViewFactory(ViewFactory.ViewMode.HYBRID, 
                                             hybridWindow.getOutputArea(), 
                                             hybridWindow.getCardPanel(),
                                             hybridWindow.getHandPanel(),
                                             hybridWindow.getOffersPanel(),
                                             hybridWindow.getInteractionPanel());
                break;
            default:
                gameView = new GameView();
                roundView = new RoundView();
                viewFactory = new ViewFactory(ViewFactory.ViewMode.CONSOLE);
        }

        
        GameController controller = new GameController(model, gameView, roundView, viewFactory);
        controller.startGame();
    }
}
