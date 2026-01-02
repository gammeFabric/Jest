package view.gui;

import model.cards.Card;
import model.players.Offer;

import javax.swing.*;
import java.awt.*;

public class OfferComponent extends JPanel {
    private final Offer offer;
    private final boolean selectable;
    private boolean selected;
    private CardButton faceUpButton;
    private CardButton faceDownButton;

    public interface OfferSelectionListener {
        void onOfferSelected(Offer offer);
    }

    public OfferComponent(Offer offer, boolean selectable, OfferSelectionListener listener) {
        this.offer = offer;
        this.selectable = selectable;
        this.selected = false;
        
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Player name label
        JLabel playerLabel = new JLabel(offer.getOwner().getName(), SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        playerLabel.setForeground(Color.WHITE);
        add(playerLabel, BorderLayout.NORTH);

        // Cards panel
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cardsPanel.setOpaque(false);

        // Face up card
        if (offer.getFaceUpCard() != null) {
            CardButton.CardSelectionListener cardListener = selectable && listener != null ? 
                index -> listener.onOfferSelected(offer) : null;
            faceUpButton = new CardButton(offer.getFaceUpCard(), true, 0, cardListener);
            cardsPanel.add(faceUpButton);
        }

        // Face down card
        if (offer.getFaceDownCard() != null) {
            faceDownButton = new CardButton(offer.getFaceDownCard(), false, 1, null);
            cardsPanel.add(faceDownButton);
        }

        add(cardsPanel, BorderLayout.CENTER);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (faceUpButton != null) {
            faceUpButton.setSelected(selected);
        }
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    public Offer getOffer() {
        return offer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (selected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(255, 255, 0, 150));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2d.dispose();
        }
    }
}

