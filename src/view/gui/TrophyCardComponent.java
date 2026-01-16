package view.gui;

import model.cards.Card;
import model.cards.TrophyType;
import model.cards.Suit;

import javax.swing.*;
import java.awt.*;

/**
 * Composant GUI pour afficher une carte trophée.
 * 
 * <p>Ce composant dessine visuellement une carte trophée avec ses informations
 * spécifiques (type de trophée, couleur, valeur).</p>
 * 
 * <p><b>Types de trophées :</b></p>
 * <ul>
 *   <li>Plus haute/basse carte d'une couleur</li>
 *   <li>Majorité de cartes d'une valeur</li>
 *   <li>Possession du Joker</li>
 *   <li>Meilleur Jest</li>
 * </ul>
 */
public class TrophyCardComponent extends JPanel {
    private final Card trophy;
    private static final int CARD_WIDTH = 120;
    private static final int CARD_HEIGHT = 160;

    public TrophyCardComponent(Card trophy) {
        this.trophy = trophy;
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String label = "Trophy";
        int labelWidth = fm.stringWidth(label);
        g2d.drawString(label, (getWidth() - labelWidth) / 2, 20);

        
        if (trophy != null && trophy.getTrophyType() != null) {
            drawTrophySymbol(g2d, trophy.getTrophyType(), trophy.getTrophySuit());
        } else {
            
            drawCrown(g2d);
        }

        
        int bottomY = getHeight() - fm.getDescent() - 5;
        g2d.drawString(label, (getWidth() - labelWidth) / 2, bottomY);

        g2d.dispose();
    }

    private void drawTrophySymbol(Graphics2D g, TrophyType type, Suit suit) {
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(Color.BLACK);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (type == TrophyType.JOKER) {
            
            drawCrown(g);
        } else if (suit != null) {
            
            String suitSymbol = getSuitSymbol(suit);
            FontMetrics fm = g.getFontMetrics();
            int symbolWidth = fm.stringWidth(suitSymbol);
            g.drawString(suitSymbol, centerX - symbolWidth / 2, centerY + fm.getAscent() / 2);
        } else {
            
            drawCrown(g);
        }
    }

    private void drawCrown(Graphics2D g) {
        g.setColor(new Color(255, 215, 0)); 
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int crownWidth = 50;
        int crownHeight = 40;

        
        int[] xPoints = {
            centerX - crownWidth / 2,
            centerX - crownWidth / 4,
            centerX,
            centerX + crownWidth / 4,
            centerX + crownWidth / 2,
            centerX + crownWidth / 3,
            centerX,
            centerX - crownWidth / 3
        };
        int[] yPoints = {
            centerY - crownHeight / 2,
            centerY - crownHeight / 2 - 10,
            centerY - crownHeight / 2,
            centerY - crownHeight / 2 - 10,
            centerY - crownHeight / 2,
            centerY + crownHeight / 2,
            centerY + crownHeight / 2 - 5,
            centerY + crownHeight / 2
        };
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, xPoints.length);
    }

    private String getSuitSymbol(Suit suit) {
        return switch (suit) {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
        };
    }
}

