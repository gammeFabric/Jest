package view.gui;

import model.cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CardButton extends JPanel {
    private final CardComponent cardComponent;
    private final int index;
    private final CardSelectionListener listener;
    private boolean hovered;

    public interface CardSelectionListener {
        void onCardSelected(int index);
    }

    public CardButton(Card card, boolean faceUp, int index, CardSelectionListener listener) {
        this.index = index;
        this.listener = listener;
        this.hovered = false;
        this.cardComponent = new CardComponent(card, faceUp, true);
        
        setLayout(new BorderLayout());
        setOpaque(false);
        add(cardComponent, BorderLayout.CENTER);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onCardSelected(index);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                repaint();
            }
        });
    }

    public void setSelected(boolean selected) {
        cardComponent.setSelected(selected);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hovered) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.dispose();
        }
    }
}

