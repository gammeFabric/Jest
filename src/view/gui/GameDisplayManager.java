package view.gui;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameDisplayManager {
    private final JPanel offersPanel;
    private final JPanel handPanel;

    public GameDisplayManager(JPanel offersPanel, JPanel handPanel) {
        this.offersPanel = offersPanel;
        this.handPanel = handPanel;
    }

    public void displayOffers(List<Offer> offers) {
        SwingUtilities.invokeLater(() -> {
            offersPanel.removeAll();
            for (Offer offer : offers) {
                if (offer != null && offer.isComplete()) {
                    OfferComponent offerComp = new OfferComponent(offer, false, null);
                    offersPanel.add(offerComp);
                }
            }
            offersPanel.revalidate();
            offersPanel.repaint();
        });
    }

    public void displayPlayerHand(Player player, ArrayList<Card> hand) {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();
            if (hand != null) {
                for (Card card : hand) {
                    CardComponent cardComp = new CardComponent(card, true, false);
                    handPanel.add(cardComp);
                }
            }
            handPanel.revalidate();
            handPanel.repaint();
        });
    }

    public void clearHand() {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();
            handPanel.revalidate();
            handPanel.repaint();
        });
    }

    public void clearOffers() {
        SwingUtilities.invokeLater(() -> {
            offersPanel.removeAll();
            offersPanel.revalidate();
            offersPanel.repaint();
        });
    }
}

