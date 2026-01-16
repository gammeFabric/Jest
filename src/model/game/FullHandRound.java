package model.game;

import model.cards.Deck;
import model.players.Player;

import java.util.ArrayList;


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
