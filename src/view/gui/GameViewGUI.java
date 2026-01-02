package view.gui;

import model.cards.ExtensionCard;
import model.players.Player;
import model.players.strategies.StrategyType;
import app.GameLauncher;
import view.interfaces.IGameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GameViewGUI implements IGameView {
    private final JFrame mainFrame;
    private final JTextArea outputArea;
    private final JPanel cardPanel;
    private String inputResult;
    private boolean waitingForInput;

    private final ArrayList<String> finalScoreLines = new ArrayList<>();

    private volatile JDialog activeDialog;

    public GameViewGUI(JFrame mainFrame, JTextArea outputArea, JPanel cardPanel) {
        this.mainFrame = mainFrame;
        this.outputArea = outputArea;
        this.cardPanel = cardPanel;
        this.waitingForInput = false;
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public void cancelActiveDialog() {
        JDialog dialog = activeDialog;
        if (dialog == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                dialog.setVisible(false);
                dialog.dispose();
            } finally {
                if (activeDialog == dialog) {
                    activeDialog = null;
                }
            }
        });
    }

    private String showInputDialog(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        pane.setWantsInput(true);

        JDialog dialog = pane.createDialog(mainFrame, title);
        activeDialog = dialog;

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (activeDialog == dialog) {
                    activeDialog = null;
                }
            }
        });

        dialog.setVisible(true);

        Object input = pane.getInputValue();
        if (input == null || input == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        if (activeDialog == dialog) {
            activeDialog = null;
        }
        return input.toString();
    }

    private int showOptionDialog(String message, String title, String[] options) {
        JOptionPane pane = new JOptionPane(
                message,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                options.length > 0 ? options[0] : null
        );

        JDialog dialog = pane.createDialog(mainFrame, title);
        activeDialog = dialog;

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (activeDialog == dialog) {
                    activeDialog = null;
                }
            }
        });

        dialog.setVisible(true);

        Object selected = pane.getValue();
        if (activeDialog == dialog) {
            activeDialog = null;
        }
        if (selected == null) {
            return -1;
        }

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selected)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int askNumberOfPlayers() {
        while (true) {
            String input = showInputDialog("Enter the number of players (3-4):", "Number of Players");
            if (input == null) {
                System.exit(0);
            }
            try {
                int playerCount = Integer.parseInt(input);
                if (playerCount >= 3 && playerCount <= 4) {
                    appendOutput("Number of players: " + playerCount);
                    return playerCount;
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Please enter a number between 3 and 4.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public String askPlayerName(int playerNumber) {
        String name = showInputDialog("Enter name for player " + playerNumber + ":", "Player Name");
        if (name == null || name.trim().isEmpty()) {
            name = "Player" + playerNumber;
        }
        appendOutput("Player " + playerNumber + ": " + name);
        return name.trim();
    }

    @Override
    public StrategyType askStrategy(String name) {
        StrategyType[] strategies = StrategyType.values();
        String[] options = new String[strategies.length];
        for (int i = 0; i < strategies.length; i++) {
            options[i] = (i + 1) + ". " + strategies[i];
        }

        int choice = showOptionDialog("Choose strategy for " + name + ":", "Strategy Selection", options);
        if (choice < 0) {
            choice = 0;
        }
        appendOutput("Strategy for " + name + ": " + strategies[choice]);
        return strategies[choice];
    }

    @Override
    public boolean isHumanPlayer(String name) {
        String[] options = {"Human", "Virtual"};
        int choice = showOptionDialog("Is " + name + " a Human or Virtual player?", "Player Type", options);
        boolean isHuman = (choice == 0);
        appendOutput(name + " is a " + (isHuman ? "Human" : "Virtual") + " player");
        return isHuman;
    }

    @Override
    public void showPlayers(List<Player> players) {
        StringBuilder sb = new StringBuilder("Players added: " + players.size() + "\n");
        for (Player player : players) {
            sb.append("- ").append(player.getName()).append("\n");
        }
        appendOutput(sb.toString());
    }

    @Override
    public void showRound(int roundNumber) {
        finalScoreLines.clear();
        appendOutput("\n=========================");
        appendOutput("      ROUND " + roundNumber);
        appendOutput("=========================");
    }

    @Override
    public void showTrophies(String trophiesInfo) {
        appendOutput("Trophies selected: ");
        appendOutput(trophiesInfo);
    }

    @Override
    public void showScore(Player player) {
        String line = player.getName() + " has " + player.getScore() + " points.";
        finalScoreLines.add(line);
        appendOutput(line);
    }

    @Override
    public void showWinner(Player winner) {
        if (winner != null) {
            appendOutput("Winner is " + winner.getName());
        } else {
            appendOutput("There is no winner");
        }
    }

    @Override
    public void showWinners(List<Player> winners) {
        if (winners == null || winners.isEmpty()) {
            appendOutput("There is no winner");
        } else if (winners.size() == 1) {
            appendOutput("Winner is " + winners.getFirst().getName());
        } else {
            StringBuilder sb = new StringBuilder("It's a tie between: ");
            for (int i = 0; i < winners.size(); i++) {
                sb.append(winners.get(i).getName());
                if (i < winners.size() - 1) {
                    sb.append(", ");
                }
            }
            appendOutput(sb.toString());
        }

        // End-of-game dialog (GUI only)
        SwingUtilities.invokeLater(() -> {
            StringBuilder message = new StringBuilder();
            if (winners == null || winners.isEmpty()) {
                message.append("No winner.\n\n");
            } else if (winners.size() == 1) {
                message.append("Winner: ").append(winners.getFirst().getName()).append("\n\n");
            } else {
                message.append("Winners: ");
                for (int i = 0; i < winners.size(); i++) {
                    message.append(winners.get(i).getName());
                    if (i < winners.size() - 1) {
                        message.append(", ");
                    }
                }
                message.append("\n\n");
            }

            if (!finalScoreLines.isEmpty()) {
                message.append("Final scores:\n");
                for (String line : finalScoreLines) {
                    message.append("- ").append(line).append("\n");
                }
                message.append("\n");
            }

            String[] options = {"New Game", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                    mainFrame,
                    message.toString(),
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                try {
                    mainFrame.dispose();
                } finally {
                    Thread t = new Thread(() -> GameLauncher.main(new String[0]), "Restart-GameLauncher");
                    t.setDaemon(false);
                    t.start();
                }
            } else {
                System.exit(0);
            }
        });
    }

    @Override
    public void showEndRoundMessage() {
        appendOutput("Game ended successfully.");
        appendOutput("We can assign all trophies to get the winner");
    }

    @Override
    public ArrayList<Integer> askForExtensions(ArrayList<ExtensionCard> availableExtensions) {
        if (availableExtensions == null || availableExtensions.isEmpty()) {
            return new ArrayList<>();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        for (ExtensionCard ext : availableExtensions) {
            JCheckBox cb = new JCheckBox(ext.getName() + " [Val: " + ext.getFaceValue() + "] - " + ext.getDescription());
            checkBoxes.add(cb);
            panel.add(cb);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(640, 220));

        JOptionPane pane = new JOptionPane(
                scrollPane,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        JDialog dialog = pane.createDialog(mainFrame, "Select Extra Cards");
        activeDialog = dialog;

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (activeDialog == dialog) {
                    activeDialog = null;
                }
            }
        });

        dialog.setVisible(true);

        Object paneValue = pane.getValue();
        if (activeDialog == dialog) {
            activeDialog = null;
        }

        int result;
        if (paneValue instanceof Integer) {
            result = (Integer) paneValue;
        } else {
            result = JOptionPane.CANCEL_OPTION;
        }

        ArrayList<Integer> selected = new ArrayList<>();
        if (result != JOptionPane.OK_OPTION) {
            appendOutput("No extensions selected. Standard game.");
            return selected;
        }

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selected.add(i);
            }
        }

        if (selected.isEmpty()) {
            appendOutput("No extensions selected. Standard game.");
        } else {
            appendOutput("Extensions selected: " + selected);
        }

        return selected;
    }

    @Override
    public void showInvalidExtensionMessage(String message){
        JOptionPane.showMessageDialog(mainFrame, message, "Invalid Extension Configuration", JOptionPane.WARNING_MESSAGE);
        appendOutput("Invalid extension configuration: " + message);
    }

    @Override
    public boolean askSaveAfterRound() {
        String[] options = {"Save", "Don't Save"};
        int choice = showOptionDialog("Save game now?", "Save", options);
        return choice == 0;
    }

    @Override
    public String askSaveName() {
        return showInputDialog("Enter save name (optional, blank for timestamp):", "Save Name");
    }

}

