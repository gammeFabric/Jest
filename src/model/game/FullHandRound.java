package model.game;

import model.cards.Deck;
import model.players.Player;

import java.util.ArrayList;

/**
 * Extension de Round pour la variante "Full Hand".
 * 
 * <p>Cette classe modifie le comportement standard d'un tour pour implémenter
 * la variante où toutes les cartes sont distribuées au début.</p>
 * 
 * <p><b>Différences avec Round standard :</b></p>
 * <ul>
 *   <li>Distribution : Toutes les cartes au lieu de 2 par joueur</li>
 *   <li>Tours multiples : Plusieurs cycles d'offres et de choix</li>
 *   <li>Fin de partie : Quand chaque joueur n'a plus qu'1 carte</li>
 *   <li>Dernière carte : Ajoutée automatiquement au Jest</li>
 * </ul>
 * 
 * <p><b>Méthodes spécifiques :</b></p>
 * <ul>
 *   <li><code>distributeAllCards()</code> - Distribution équitable de toutes les cartes</li>
 *   <li><code>finalizeRound()</code> - Traitement des dernières cartes</li>
 *   <li><code>isOver()</code> - Vérifie si tous ont ≤1 carte</li>
 * </ul>
 * 
 * @see model.game.Round
 * @see model.game.variants.FullHandVariant
 */
public class FullHandRound extends Round {
    private static final long serialVersionUID = 1L;
    private boolean initialDistributionDone = false;

    public FullHandRound(ArrayList<Player> players, Deck deck) {
        super(players, deck);
    }

    
    public void distributeAllCards() {
        if (initialDistributionDone) {
            return; 
        }

        int totalCards = getDeck().getRemainingCount();
        int playerCount = getPlayers().size();
        int cardsPerPlayer = totalCards / playerCount;
        int remainingCards = totalCards % playerCount;

        
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player player : getPlayers()) {
                if (!getDeck().isEmpty()) {
                    player.addToHand(getDeck().dealCard());
                }
            }
        }

        
        for (int i = 0; i < remainingCards; i++) {
            if (!getDeck().isEmpty()) {
                getPlayers().get(i).addToHand(getDeck().dealCard());
            }
        }

        initialDistributionDone = true;
    }

    
    @Override
    public boolean isOver() {
        
        for (Player player : getPlayers()) {
            if (player.getHand().size() > 1) {
                return false;
            }
        }
        return true;
    }

    
    public void finalizeRound() {
        for (Player player : getPlayers()) {
            if (player.getHand().size() == 1) {
                
                player.getJest().addCard(player.getHand().get(0));
                player.getHand().clear(); 
            }
        }
        setIsOver(true);
    }

    
    @Override
    public void dealCards() {
        
        
    }

    
    public int getInitialCardsPerPlayer() {
        if (initialDistributionDone) {
            return getPlayers().isEmpty() ? 0 : 
                   getPlayers().get(0).getHand().size();
        }
        
        int totalCards = getDeck().getRemainingCount();
        return totalCards / getPlayers().size();
    }

    
    public boolean isInitialDistributionDone() {
        return initialDistributionDone;
    }
}
