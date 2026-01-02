package view.hybrid;

import model.cards.ExtensionCard;
import model.players.Player;
import model.players.strategies.StrategyType;
import view.console.GameView;
import view.gui.GameViewGUI;
import view.interfaces.IGameView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameViewHybrid implements IGameView {
    private final GameView consoleView;
    private final GameViewGUI guiView;

    static final BufferedReader CONSOLE_READER = new BufferedReader(new InputStreamReader(System.in));

    private static final class Choice<T> {
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

    static String readLineNonBlocking(Choice<?> choice, String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }

        while (!choice.isResolved()) {
            try {
                if (CONSOLE_READER.ready()) {
                    return CONSOLE_READER.readLine();
                }
            } catch (IOException e) {
                return null;
            }

            synchronized (choice.lock) {
                if (choice.isResolved()) {
                    return null;
                }
                try {
                    choice.lock.wait(75);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    private static void runGuiAsync(String threadName, Runnable action) {
        Thread t = new Thread(action, threadName);
        t.setDaemon(true);
        t.start();
    }

    public GameViewHybrid(GameView consoleView, GameViewGUI guiView) {
        this.consoleView = consoleView;
        this.guiView = guiView;
    }

    @Override
    public int askNumberOfPlayers() {
        Choice<Integer> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-AskNumberOfPlayers", () -> {
            int result = guiView.askNumberOfPlayers();
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Enter the number of players (3-4): ");
                if (line == null) {
                    return;
                }
                try {
                    int playerCount = Integer.parseInt(line.trim());
                    if (playerCount >= 3 && playerCount <= 4) {
                        choice.resolve(playerCount);
                        guiView.cancelActiveDialog();
                        return;
                    } else {
                        System.out.println("Please enter a number between 3 and 4.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
        }, "Hybrid-Console-AskNumberOfPlayers");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }

    @Override
    public String askPlayerName(int playerNumber) {
        Choice<String> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-AskPlayerName", () -> {
            String result = guiView.askPlayerName(playerNumber);
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Enter name for player " + playerNumber + ": ");
                if (line == null) {
                    return;
                }
                String name = line.trim();
                if (name.isEmpty()) {
                    name = "Player" + playerNumber;
                }
                choice.resolve(name);
                guiView.cancelActiveDialog();
                return;
            }
        }, "Hybrid-Console-AskPlayerName");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }

    @Override
    public StrategyType askStrategy(String name) {
        Choice<StrategyType> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-AskStrategy", () -> {
            StrategyType result = guiView.askStrategy(name);
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            StrategyType[] values = StrategyType.values();
            System.out.println("Choose strategy for " + name + ":");
            for (StrategyType strategy : values) {
                System.out.println(strategy.ordinal() + 1 + ". " + strategy);
            }

            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Choose the number of the strategy: ");
                if (line == null) {
                    return;
                }
                try {
                    int idx = Integer.parseInt(line.trim());
                    if (idx >= 1 && idx <= values.length) {
                        choice.resolve(values[idx - 1]);
                        guiView.cancelActiveDialog();
                        return;
                    }
                } catch (NumberFormatException ignored) {
                }
                System.out.println("Please enter a valid number.");
            }
        }, "Hybrid-Console-AskStrategy");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }

    @Override
    public boolean isHumanPlayer(String name) {
        Choice<Boolean> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-IsHumanPlayer", () -> {
            boolean result = guiView.isHumanPlayer(name);
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Is " + name + " a Human or Virtual player? (H/V): ");
                if (line == null) {
                    return;
                }
                String type = line.trim().toUpperCase();
                if (type.equals("H")) {
                    choice.resolve(true);
                    guiView.cancelActiveDialog();
                    return;
                }
                if (type.equals("V")) {
                    choice.resolve(false);
                    guiView.cancelActiveDialog();
                    return;
                }
                System.out.println("Please enter 'H' for Human or 'V' for Virtual.");
            }
        }, "Hybrid-Console-IsHumanPlayer");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }

    @Override
    public ArrayList<Integer> askForExtensions(ArrayList<ExtensionCard> availableExtensions) {
        Choice<ArrayList<Integer>> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-AskForExtensions", () -> {
            ArrayList<Integer> result = guiView.askForExtensions(availableExtensions);
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            if (availableExtensions == null || availableExtensions.isEmpty()) {
                choice.resolve(new ArrayList<>());
                guiView.cancelActiveDialog();
                return;
            }

            System.out.println("\n--- MENU EXTENSIONS ---");
            System.out.println("Available extensions:");
            for (int i = 0; i < availableExtensions.size(); i++) {
                ExtensionCard ext = availableExtensions.get(i);
                System.out.println((i + 1) + ". " + ext.getName() + " [Val: " + ext.getFaceValue() + "]");
                System.out.println("   Description: " + ext.getDescription());
                System.out.println();
            }
            System.out.println("Enter the numbers of the extensions you want to add, separated by spaces (e.g., '1 3').");
            System.out.println("Press Enter to play without extensions.");

            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "> ");
                if (line == null) {
                    return;
                }

                String input = line.trim();
                ArrayList<Integer> selections = new ArrayList<>();
                if (!input.isEmpty()) {
                    String[] tokens = input.split("\\s+");
                    for (String token : tokens) {
                        try {
                            int idx = Integer.parseInt(token) - 1;
                            if (idx >= 0 && idx < availableExtensions.size()) {
                                selections.add(idx);
                            } else {
                                System.out.println("Warning: " + token + " is not a valid option number (Ignored).");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Warning: '" + token + "' is not a number (Ignored).");
                        }
                    }
                }

                choice.resolve(selections);
                guiView.cancelActiveDialog();
                return;
            }
        }, "Hybrid-Console-AskForExtensions");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }

    @Override
    public void showInvalidExtensionMessage(String message) {
        // Show error in both console and GUI
        consoleView.showInvalidExtensionMessage(message);
        guiView.showInvalidExtensionMessage(message);
    }

    @Override
    public void showPlayers(List<Player> players) {
        consoleView.showPlayers(players);
        guiView.showPlayers(players);
    }

    @Override
    public void showRound(int roundNumber) {
        consoleView.showRound(roundNumber);
        guiView.showRound(roundNumber);
    }

    @Override
    public void showTrophies(String trophiesInfo) {
        consoleView.showTrophies(trophiesInfo);
        guiView.showTrophies(trophiesInfo);
    }

    @Override
    public void showScore(Player player) {
        consoleView.showScore(player);
        guiView.showScore(player);
    }

    @Override
    public void showWinner(Player winner) {
        consoleView.showWinner(winner);
        guiView.showWinner(winner);
    }

    @Override
    public void showWinners(List<Player> winners) {
        consoleView.showWinners(winners);
        guiView.showWinners(winners);
    }

    @Override
    public void showEndRoundMessage() {
        consoleView.showEndRoundMessage();
        guiView.showEndRoundMessage();
    }

    @Override
    public boolean askSaveAfterRound() {
        Choice<Boolean> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-AskSaveAfterRound", () -> {
            boolean result = guiView.askSaveAfterRound();
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Save game now? (Y/N): ");
                if (line == null) {
                    return;
                }
                String input = line.trim().toUpperCase();
                if (input.equals("Y")) {
                    choice.resolve(true);
                    guiView.cancelActiveDialog();
                    return;
                }
                if (input.equals("N")) {
                    choice.resolve(false);
                    guiView.cancelActiveDialog();
                    return;
                }
                System.out.println("Please enter 'Y' or 'N'.");
            }
        }, "Hybrid-Console-AskSaveAfterRound");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }

    @Override
    public String askSaveName() {
        Choice<String> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-AskSaveName", () -> {
            String result = guiView.askSaveName();
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            String line = readLineNonBlocking(choice, "Enter save name (optional, press Enter for timestamp): ");
            if (line == null) {
                return;
            }
            choice.resolve(line);
            guiView.cancelActiveDialog();
        }, "Hybrid-Console-AskSaveName");
        consoleThread.setDaemon(true);
        consoleThread.start();

        return choice.await();
    }
}