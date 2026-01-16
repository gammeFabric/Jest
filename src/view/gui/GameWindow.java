package view.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'interface GUI.
 * 
 * <p>Cette classe crée et gère la fenêtre principale de la partie
 * avec tous ses panneaux et composants d'affichage.</p>
 * 
 * <p><b>Panneaux principaux :</b></p>
 * <ul>
 *   <li>Zone de sortie texte (messages)</li>
 *   <li>Zone des cartes</li>
 *   <li>Zone des offres</li>
 *   <li>Main du joueur</li>
 *   <li>Zones des bots</li>
 *   <li>Panneau des trophées</li>
 * </ul>
 */
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

    
    private static final Color LIGHT_BLUE_BG = new Color(173, 216, 230);

    public GameWindow() {
        frame = new JFrame("Jest Card Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        frame.setLayout(new BorderLayout());
        frame.setBackground(LIGHT_BLUE_BG);

        
        headerLabel = new JLabel("Round 1 - Variant: Standard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerLabel.setOpaque(false);

        
        gameAreaPanel = new JPanel(new BorderLayout());
        gameAreaPanel.setBackground(LIGHT_BLUE_BG);
        gameAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        botAreasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        botAreasPanel.setBackground(LIGHT_BLUE_BG);
        botAreasPanel.setPreferredSize(new Dimension(0, 200));

        
        JPanel middleSection = new JPanel(new BorderLayout());
        middleSection.setBackground(LIGHT_BLUE_BG);
        middleSection.setOpaque(false);

        
        deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        deckPanel.setBackground(LIGHT_BLUE_BG);
        deckPanel.setPreferredSize(new Dimension(300, 0));

        
        trophiesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        trophiesPanel.setBackground(LIGHT_BLUE_BG);
        trophiesPanel.setPreferredSize(new Dimension(600, 0));

        middleSection.add(deckPanel, BorderLayout.WEST);
        middleSection.add(trophiesPanel, BorderLayout.CENTER);

        
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        handPanel.setBackground(LIGHT_BLUE_BG);
        handPanel.setPreferredSize(new Dimension(0, 180));


        
        offersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        offersPanel.setBackground(LIGHT_BLUE_BG);
        offersPanel.setOpaque(false);

        
        cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardPanel.setBackground(LIGHT_BLUE_BG);
        cardPanel.setOpaque(false);

        
        interactionPanel = new InteractionPanel(this);
        interactionPanel.setPreferredSize(new Dimension(0, 200));

        
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

        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        outputArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        
        
        scrollPane.setViewportView(outputArea);

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LIGHT_BLUE_BG);
        topPanel.add(headerLabel, BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameAreaPanel, scrollPane);
        mainSplit.setDividerLocation(1100);
        mainSplit.setDividerSize(5);
        mainSplit.setResizeWeight(1.0);
        
        
        mainSplit.setLeftComponent(gameAreaPanel);
        mainSplit.setRightComponent(scrollPane);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplit, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        
        
        cleanupRightPanel();
    }


    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            
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

    
    public void clearAllPanels() {
        clearHandPanel();
        clearOffersPanel();
        clearBotAreasPanel();
        clearDeckPanel();
        clearTrophiesPanel();
        
        
        headerLabel.setText("Round 1 - Variant: Standard");
        headerLabel.revalidate();
        headerLabel.repaint();
    }

    public InteractionPanel getInteractionPanel() {
        return interactionPanel;
    }
    
    
    public void cleanupRightPanel() {
        SwingUtilities.invokeLater(() -> {
            
            Component rightComponent = ((JSplitPane) frame.getContentPane().getComponent(1)).getRightComponent();
            if (rightComponent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) rightComponent;
                
                if (scrollPane.getViewport().getView() != outputArea) {
                    scrollPane.setViewportView(outputArea);
                }
            }
        });
    }
}
