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
    private final JPanel botAreasPanel;
    private final JPanel deckPanel;
    private final JPanel trophiesPanel;
    private final boolean isHybridMode;

    public GameDisplayManager(JPanel offersPanel, JPanel handPanel) {
        this(offersPanel, handPanel, null, null, null, false);
    }

    public GameDisplayManager(JPanel offersPanel, JPanel handPanel, 
                             JPanel botAreasPanel, JPanel deckPanel, JPanel trophiesPanel) {
        this(offersPanel, handPanel, botAreasPanel, deckPanel, trophiesPanel, false);
    }
    
    public GameDisplayManager(JPanel offersPanel, JPanel handPanel, 
                             JPanel botAreasPanel, JPanel deckPanel, JPanel trophiesPanel, boolean isHybridMode) {
        this.offersPanel = offersPanel;
        this.handPanel = handPanel;
        this.botAreasPanel = botAreasPanel;
        this.deckPanel = deckPanel;
        this.trophiesPanel = trophiesPanel;
        this.isHybridMode = isHybridMode;
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

    public void displayBots(List<Player> bots, List<Offer> offers) {
        if (botAreasPanel == null) return;
        
        SwingUtilities.invokeLater(() -> {
            botAreasPanel.removeAll();
            for (Player bot : bots) {
                
                Offer botOffer = null;
                if (offers != null) {
                    for (Offer offer : offers) {
                        if (offer != null && offer.getOwner() == bot) {
                            botOffer = offer;
                            break;
                        }
                    }
                }
                BotAreaComponent botArea = new BotAreaComponent(bot, botOffer);
                botAreasPanel.add(botArea);
            }
            botAreasPanel.revalidate();
            botAreasPanel.repaint();
        });
    }

    public void displayTrophies(List<Card> trophies) {
        if (trophiesPanel == null) return;
        
        SwingUtilities.invokeLater(() -> {
            trophiesPanel.removeAll();
            if (trophies != null) {
                for (Card trophy : trophies) {
                    TrophyCardComponent trophyCard = new TrophyCardComponent(trophy);
                    trophiesPanel.add(trophyCard);
                }
            }
            trophiesPanel.revalidate();
            trophiesPanel.repaint();
        });
    }

    public void displayDeck(int deckSize) {
        if (deckPanel == null) return;
        
        SwingUtilities.invokeLater(() -> {
            deckPanel.removeAll();
            
            
            int cardsToShow = Math.min(deckSize, 10); 
            for (int i = 0; i < cardsToShow; i++) {
                CardComponent card = new CardComponent(null, false, false);
                
                card.setBorder(BorderFactory.createEmptyBorder(0, 0, i * 2, 0));
                deckPanel.add(card);
            }
            
            deckPanel.revalidate();
            deckPanel.repaint();
        });
    }

    public void displayPlayerHand(Player player, ArrayList<Card> hand) {
        
        
        if (isHybridMode) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();
            if (hand != null) {
                
                for (int i = 0; i < hand.size(); i++) {
                    Card card = hand.get(i);
                    CardComponent cardComp = new CardComponent(card, true, false);
                    
                    int offset = (i - hand.size() / 2) * 3;
                    cardComp.setBorder(BorderFactory.createEmptyBorder(0, Math.max(0, offset), 0, Math.max(0, -offset)));
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

    public void clearBots() {
        if (botAreasPanel != null) {
            SwingUtilities.invokeLater(() -> {
                botAreasPanel.removeAll();
                botAreasPanel.revalidate();
                botAreasPanel.repaint();
            });
        }
    }
}
