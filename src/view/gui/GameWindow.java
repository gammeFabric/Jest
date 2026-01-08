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
    private final JPanel botAreasPanel;
    private final JPanel deckPanel;
    private final JPanel trophiesPanel;
        private final JLabel headerLabel;
    private final InteractionPanel interactionPanel;

    // Light blue background color matching the image
    private static final Color LIGHT_BLUE_BG = new Color(173, 216, 230);

    public GameWindow() {
        frame = new JFrame("Jest Card Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        frame.setLayout(new BorderLayout());
        frame.setBackground(LIGHT_BLUE_BG);

        // Header panel with round and variant info
        headerLabel = new JLabel("Round 1 - Variant: Standard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerLabel.setOpaque(false);

        // Main game area with new layout
        gameAreaPanel = new JPanel(new BorderLayout());
        gameAreaPanel.setBackground(LIGHT_BLUE_BG);
        gameAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Bot areas (3 bots showing their offers)
        botAreasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        botAreasPanel.setBackground(LIGHT_BLUE_BG);
        botAreasPanel.setPreferredSize(new Dimension(0, 200));

        // Middle section: Left (deck) and Right (trophies)
        JPanel middleSection = new JPanel(new BorderLayout());
        middleSection.setBackground(LIGHT_BLUE_BG);
        middleSection.setOpaque(false);

        // Left: Deck area (two stacks of face-down cards)
        deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        deckPanel.setBackground(LIGHT_BLUE_BG);
        deckPanel.setPreferredSize(new Dimension(300, 0));

        // Right: Trophy cards (4 cards horizontally)
        trophiesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        trophiesPanel.setBackground(LIGHT_BLUE_BG);
        trophiesPanel.setPreferredSize(new Dimension(600, 0));

        middleSection.add(deckPanel, BorderLayout.WEST);
        middleSection.add(trophiesPanel, BorderLayout.CENTER);

        // Bottom: Player's hand (fanned out cards)
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        handPanel.setBackground(LIGHT_BLUE_BG);
        handPanel.setPreferredSize(new Dimension(0, 180));


        // Offers panel (for displaying available offers during choosing phase)
        offersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        offersPanel.setBackground(LIGHT_BLUE_BG);
        offersPanel.setOpaque(false);

        // Card display panel (for temporary card displays)
        cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardPanel.setBackground(LIGHT_BLUE_BG);
        cardPanel.setOpaque(false);

        // Interaction panel for non-blocking player interactions
        interactionPanel = new InteractionPanel(this);
        interactionPanel.setPreferredSize(new Dimension(0, 200));

        // Layout structure
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(LIGHT_BLUE_BG);
        centerPanel.setOpaque(false);
        
        centerPanel.add(botAreasPanel, BorderLayout.NORTH);
        JPanel mainGamePanel = new JPanel(new BorderLayout());
        mainGamePanel.setBackground(LIGHT_BLUE_BG);
        mainGamePanel.setOpaque(false);
        mainGamePanel.add(middleSection, BorderLayout.CENTER);
        mainGamePanel.add(handPanel, BorderLayout.SOUTH);
        
        centerPanel.add(mainGamePanel, BorderLayout.CENTER);
        centerPanel.add(interactionPanel, BorderLayout.SOUTH);

        gameAreaPanel.add(centerPanel, BorderLayout.CENTER);

        // Output/log area (smaller, on the right)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        outputArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        
        // Ensure only the output area is in the scroll pane
        scrollPane.setViewportView(outputArea);

        // Main layout: Header, Game Area, Log
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LIGHT_BLUE_BG);
        topPanel.add(headerLabel, BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameAreaPanel, scrollPane);
        mainSplit.setDividerLocation(1100);
        mainSplit.setDividerSize(5);
        mainSplit.setResizeWeight(1.0);
        
        // Ensure only the intended components are in the split pane
        mainSplit.setLeftComponent(gameAreaPanel);
        mainSplit.setRightComponent(scrollPane);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplit, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        
        // Clean up any unwanted components
        cleanupRightPanel();
    }


    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            // Clean up any unwanted components after window is visible
            cleanupRightPanel();
        });
    }

    public void updateHeader(int roundNumber, String variantName) {
        SwingUtilities.invokeLater(() -> {
            headerLabel.setText("Round " + roundNumber + " - Variant: " + variantName);
        });
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

    public JPanel getBotAreasPanel() {
        return botAreasPanel;
    }

    public JPanel getDeckPanel() {
        return deckPanel;
    }

    public JPanel getTrophiesPanel() {
        return trophiesPanel;
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

    public void clearBotAreasPanel() {
        botAreasPanel.removeAll();
        botAreasPanel.revalidate();
        botAreasPanel.repaint();
    }

    public void clearDeckPanel() {
        deckPanel.removeAll();
        deckPanel.revalidate();
        deckPanel.repaint();
    }

    public void clearTrophiesPanel() {
        trophiesPanel.removeAll();
        trophiesPanel.revalidate();
        trophiesPanel.repaint();
    }

    /**
     * Clears all game panels for restart functionality.
     * This method is called when restarting a game to clear the UI state.
     */
    public void clearAllPanels() {
        clearHandPanel();
        clearOffersPanel();
        clearBotAreasPanel();
        clearDeckPanel();
        clearTrophiesPanel();
        
        // Reset header label
        headerLabel.setText("Round 1 - Variant: Standard");
        headerLabel.revalidate();
        headerLabel.repaint();
    }

    public InteractionPanel getInteractionPanel() {
        return interactionPanel;
    }
    
    /**
     * Removes any unwanted components from the right panel (scroll pane area).
     * This ensures only the Game Log is displayed as intended.
     */
    public void cleanupRightPanel() {
        SwingUtilities.invokeLater(() -> {
            // Get the scroll pane from the split pane
            Component rightComponent = ((JSplitPane) frame.getContentPane().getComponent(1)).getRightComponent();
            if (rightComponent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) rightComponent;
                // Ensure only the output area is in the viewport
                if (scrollPane.getViewport().getView() != outputArea) {
                    scrollPane.setViewportView(outputArea);
                }
            }
        });
    }
}
