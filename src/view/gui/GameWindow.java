package view.gui;

import javax.swing.*;
import java.awt.*;

public class GameWindow {
    private final JFrame frame;
    private final JTextArea outputArea;
    private final JPanel cardPanel;
    private final JPanel gameAreaPanel;
    private final JPanel offersPanel;
    private final JPanel handPanel;

    public GameWindow() {
        frame = new JFrame("Jest Card Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        frame.setLayout(new BorderLayout());
        frame.setBackground(new Color(0, 100, 0)); // Green table background

        // Main game area with card table layout
        gameAreaPanel = new JPanel(new BorderLayout());
        gameAreaPanel.setBackground(new Color(0, 100, 0));
        gameAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Output/log area (smaller, on the right)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        outputArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));

        // Offers display panel (center - where players' offers are shown)
        offersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        offersPanel.setBackground(new Color(0, 100, 0));
        offersPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            "Player Offers", 
            0, 0, 
            new Font("Arial", Font.BOLD, 14), 
            Color.WHITE));

        // Hand panel (bottom - player's current hand)
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        handPanel.setBackground(new Color(0, 100, 0));
        handPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            "Your Hand", 
            0, 0, 
            new Font("Arial", Font.BOLD, 14), 
            Color.WHITE));
        handPanel.setPreferredSize(new Dimension(0, 180));

        // Card display panel (for temporary card displays)
        cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardPanel.setBackground(new Color(0, 100, 0));
        cardPanel.setOpaque(false);

        // Layout: Center game area, right side log
        gameAreaPanel.add(offersPanel, BorderLayout.CENTER);
        gameAreaPanel.add(handPanel, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameAreaPanel, scrollPane);
        splitPane.setDividerLocation(1100);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(1.0);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public JFrame getFrame() {
        return frame;
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public JPanel getGameAreaPanel() {
        return gameAreaPanel;
    }

    public JPanel getOffersPanel() {
        return offersPanel;
    }

    public JPanel getHandPanel() {
        return handPanel;
    }

    public void clearHandPanel() {
        handPanel.removeAll();
        handPanel.revalidate();
        handPanel.repaint();
    }

    public void clearOffersPanel() {
        offersPanel.removeAll();
        offersPanel.revalidate();
        offersPanel.repaint();
    }
}

