package view.gui;

import model.cards.Card;
import model.cards.TrophyType;
import model.cards.Suit;

import javax.swing.*;
import java.awt.*;

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

        // Yellow background (trophy card color)
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // "Trophy" label at top
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String label = "Trophy";
        int labelWidth = fm.stringWidth(label);
        g2d.drawString(label, (getWidth() - labelWidth) / 2, 20);

        // Draw trophy symbol in center
        if (trophy != null && trophy.getTrophyType() != null) {
            drawTrophySymbol(g2d, trophy.getTrophyType(), trophy.getTrophySuit());
        } else {
            // Default trophy symbol (crown)
            drawCrown(g2d);
        }

        // "Trophy" label at bottom
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
            // Draw crown symbol
            drawCrown(g);
        } else if (suit != null) {
            // Draw suit symbol
            String suitSymbol = getSuitSymbol(suit);
            FontMetrics fm = g.getFontMetrics();
            int symbolWidth = fm.stringWidth(suitSymbol);
            g.drawString(suitSymbol, centerX - symbolWidth / 2, centerY + fm.getAscent() / 2);
        } else {
            // Default: draw crown
            drawCrown(g);
        }
    }

    private void drawCrown(Graphics2D g) {
        g.setColor(new Color(255, 215, 0)); // Gold color
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int crownWidth = 50;
        int crownHeight = 40;

        // Draw crown shape
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
        
        // Outline
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

