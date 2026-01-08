package model.game;

import model.cards.Deck;
import model.players.Player;

import java.util.ArrayList;

/**
 * Custom Round implementation for the Full Hand variant.
 * This round distributes all cards at the beginning and manages the game
 * until each player has only one card left.
 */
public class FullHandRound extends Round {
    private static final long serialVersionUID = 1L;
    private boolean initialDistributionDone = false;

    public FullHandRound(ArrayList<Player> players, Deck deck) {
        super(players, deck);
    }

    /**
     * Distributes all cards fairly among players at the beginning.
     * Each player gets an equal number of cards (or as close as possible).
     */
    public void distributeAllCards() {
        if (initialDistributionDone) {
            return; // Already distributed
        }

        int totalCards = getDeck().getRemainingCount();
        int playerCount = getPlayers().size();
        int cardsPerPlayer = totalCards / playerCount;
        int remainingCards = totalCards % playerCount;

        // Distribute cards equally
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player player : getPlayers()) {
                if (!getDeck().isEmpty()) {
                    player.addToHand(getDeck().dealCard());
                }
            }
        }

        // Distribute remaining cards (if any) starting from first player
        for (int i = 0; i < remainingCards; i++) {
            if (!getDeck().isEmpty()) {
                getPlayers().get(i).addToHand(getDeck().dealCard());
            }
        }

        initialDistributionDone = true;
    }

    /**
     * Checks if the round should end based on the Full Hand variant rules.
     * The round ends when each player has only one card left.
     * @return true if the round should end
     */
    @Override
    public boolean isOver() {
        // Check if each player has only one card left
        for (Player player : getPlayers()) {
            if (player.getHand().size() > 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finalizes the round by adding each player's last card to their Jest.
     * This should be called when the round is over.
     */
    public void finalizeRound() {
        for (Player player : getPlayers()) {
            if (player.getHand().size() == 1) {
                // Add the last card to the player's Jest
                player.getJest().addCard(player.getHand().get(0));
                player.getHand().clear(); // Remove the last card from hand
            }
        }
        setIsOver(true);
    }

    /**
     * Override the standard dealCards method to do nothing,
     * as we use distributeAllCards() instead.
     */
    @Override
    public void dealCards() {
        // Cards are already distributed in distributeAllCards()
        // This method is disabled for this variant
    }

    /**
     * Gets the number of cards each player should have at the start.
     * @return average cards per player
     */
    public int getInitialCardsPerPlayer() {
        if (initialDistributionDone) {
            return getPlayers().isEmpty() ? 0 : 
                   getPlayers().get(0).getHand().size();
        }
        
        int totalCards = getDeck().getRemainingCount();
        return totalCards / getPlayers().size();
    }

    /**
     * Checks if initial distribution has been completed.
     * @return true if cards have been distributed
     */
    public boolean isInitialDistributionDone() {
        return initialDistributionDone;
    }
}
