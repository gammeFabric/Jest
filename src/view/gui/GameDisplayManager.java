package view.gui;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;

import javax.swing.*;
import java.util.List;

/**
 * Gestionnaire d'affichage du jeu en GUI.
 * 
 * <p>Cette classe gère l'affichage de tous les éléments visuels d'un tour
 * dans la vue GUI, notamment les offres, la main du joueur et les bots.</p>
 * 
 * <p><b>Éléments gérés :</b></p>
 * <ul>
 *   <li>Affichage des offres des joueurs</li>
 *   <li>Affichage de la main du joueur humain</li>
 *   <li>Affichage des zones des bots</li>
 *   <li>Affichage du deck</li>
 * </ul>
 */
public class GameDisplayManager {
    private final JPanel botAreasPanel;
    private final JPanel deckPanel;
    private final JPanel trophiesPanel;

    public GameDisplayManager(JPanel botAreasPanel, JPanel deckPanel, JPanel trophiesPanel) {
        this.botAreasPanel = botAreasPanel;
        this.deckPanel = deckPanel;
        this.trophiesPanel = trophiesPanel;
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
}
