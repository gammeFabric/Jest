package view.gui;

import model.cards.Card;
import model.cards.ExtensionCard;
import model.cards.Joker;
import model.cards.Suit;
import model.cards.SuitCard;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class CardComponent extends JPanel {
    private final Card card;
    private final boolean faceUp;
    private final boolean selectable;
    private boolean selected;
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 140;

    public CardComponent(Card card, boolean faceUp, boolean selectable) {
        this.card = card;
        this.faceUp = faceUp;
        this.selectable = selectable;
        this.selected = false;
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(false);
    }

    public CardComponent(Card card, boolean faceUp) {
        this(card, faceUp, false);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (faceUp && card != null) {
            drawFaceUpCard(g2d);
        } else {
            drawFaceDownCard(g2d);
        }

        if (selected) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
        }

        g2d.dispose();
    }

    private void drawFaceUpCard(Graphics2D g) {
        // Draw card background
        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        if (card instanceof Joker) {
            drawJoker(g);
        } else if (card instanceof SuitCard) {
            drawSuitCard(g, (SuitCard) card);
        } else if (card instanceof ExtensionCard) {
            drawExtensionCard(g, (ExtensionCard) card);
        }
    }

    private void drawExtensionCard(Graphics2D g, ExtensionCard extCard) {
        // Subtle background to distinguish extension cards
        g.setColor(new Color(245, 245, 255));
        g.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 10, 10);

        // Header band
        g.setColor(new Color(70, 70, 160));
        g.fillRoundRect(6, 6, getWidth() - 12, 26, 8, 8);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String title = extCard.getName();
        FontMetrics fmTitle = g.getFontMetrics();
        int titleMaxWidth = getWidth() - 16;
        if (fmTitle.stringWidth(title) > titleMaxWidth) {
            while (title.length() > 0 && fmTitle.stringWidth(title + "…") > titleMaxWidth) {
                title = title.substring(0, title.length() - 1);
            }
            title = title + "…";
        }
        g.drawString(title, 10, 24);

        // Value badge (top-right)
        int badgeW = 26;
        int badgeH = 18;
        int badgeX = getWidth() - badgeW - 8;
        int badgeY = 38;
        g.setColor(new Color(255, 215, 0));
        g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String val = String.valueOf(extCard.getFaceValue());
        FontMetrics fmVal = g.getFontMetrics();
        g.drawString(val, badgeX + (badgeW - fmVal.stringWidth(val)) / 2, badgeY + 13);

        // Label
        g.setColor(new Color(70, 70, 160));
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("EXT", 10, 52);

        // Description (wrapped to a few lines)
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        String desc = extCard.getDescription();
        if (desc == null) desc = "";

        int x = 10;
        int y = 68;
        int maxWidth = getWidth() - 20;
        int lineHeight = 12;
        int maxLines = 5;

        String[] words = desc.split("\\s+");
        StringBuilder line = new StringBuilder();
        int lines = 0;
        for (String w : words) {
            if (w.isEmpty()) continue;
            String candidate = line.isEmpty() ? w : line + " " + w;
            if (g.getFontMetrics().stringWidth(candidate) <= maxWidth) {
                line.setLength(0);
                line.append(candidate);
            } else {
                g.drawString(line.toString(), x, y);
                y += lineHeight;
                lines++;
                if (lines >= maxLines) {
                    g.drawString("…", x, y);
                    return;
                }
                line.setLength(0);
                line.append(w);
            }
        }
        if (!line.isEmpty() && lines < maxLines) {
            g.drawString(line.toString(), x, y);
        }
    }

    private void drawSuitCard(Graphics2D g, SuitCard suitCard) {
        Suit suit = suitCard.getSuit();
        String face = suitCard.getFace().toString();
        Color suitColor = (suit == Suit.HEARTS || suit == Suit.DIAMONDS) ? Color.RED : Color.BLACK;
        
        g.setColor(suitColor);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // Draw face value in top-left
        g.drawString(face, 8, 25);

        // Draw suit symbol
        String suitSymbol = getSuitSymbol(suit);
        Font symbolFont = new Font("Arial", Font.BOLD, 30);
        g.setFont(symbolFont);
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        FontMetrics fm = g.getFontMetrics();
        int symbolWidth = fm.stringWidth(suitSymbol);
        int symbolHeight = fm.getAscent();
        
        g.drawString(suitSymbol, centerX - symbolWidth / 2, centerY + symbolHeight / 2);

        // Draw face value in bottom-right (rotated)
        g.setFont(new Font("Arial", Font.BOLD, 20));
//        g.rotate(Math.PI, centerX, centerY);
//        g.drawString(face, 8, -getHeight() + 25);
//        g.rotate(-Math.PI, centerX, centerY);


        g.rotate(Math.PI, centerX, centerY);
        g.drawString(face, -getWidth() + 20, -10);
        g.rotate(-Math.PI, centerX, centerY);
    }

    private void drawJoker(Graphics2D g) {
        int cardWidth = getWidth();
        int cardHeight = getHeight();

        // Enable better text rendering
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw "JOKER" label at top
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, cardHeight / 10));

        FontMetrics fm = g.getFontMetrics();
        String text = "JOKER";
        int textWidth = fm.stringWidth(text);
        g.drawString(text, (cardWidth - textWidth) / 2, fm.getAscent() + 10);

        // Joker symbol
        String jokerSymbol = "🃏";

        // Start with a large font size
        Font baseFont = new Font("Segoe UI Emoji", Font.PLAIN, 1000);

        // We can have a problem here with Joker, for diff systems we would need to provide fix
        // MacOS works great, need tests on windows
//        if (!baseFont.canDisplay('\uD83C\uDCCF')) {
//            baseFont = new Font("Noto Color Emoji", Font.PLAIN, 1000);
//        }
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = baseFont.getStringBounds(jokerSymbol, frc);

        // Calculate scale to fit card
        double scaleX = (cardWidth * 0.8) / bounds.getWidth();
        double scaleY = (cardHeight * 0.8) / bounds.getHeight();
        double scale = Math.min(scaleX, scaleY);

        Font scaledFont = baseFont.deriveFont(AffineTransform.getScaleInstance(scale, scale));
        g.setFont(scaledFont);

        // Recalculate bounds after scaling
        bounds = scaledFont.getStringBounds(jokerSymbol, frc);

        int x = (int) ((cardWidth - bounds.getWidth()) / 2);
        int y = (int) ((cardHeight - bounds.getHeight()) / 2 - bounds.getY());

        g.drawString(jokerSymbol, x, y);
    }


    private void drawFaceDownCard(Graphics2D g) {
        // Draw card back
        g.setColor(new Color(0, 0, 139)); // Dark blue
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Draw pattern
        g.setColor(new Color(0, 0, 200));
        for (int i = 0; i < getWidth(); i += 10) {
            for (int j = 0; j < getHeight(); j += 10) {
                if ((i + j) % 20 == 0) {
                    g.fillOval(i, j, 5, 5);
                }
            }
        }
    }

    private String getSuitSymbol(Suit suit) {
        return switch (suit) {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
        };
    }

    public static int getCardWidth() {
        return CARD_WIDTH;
    }

    public static int getCardHeight() {
        return CARD_HEIGHT;
    }
}

