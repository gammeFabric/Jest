package model.game;

import model.cards.CardEffect;
import model.cards.ExtensionCard;
import model.cards.Joker;
import model.players.ScoreVisitorImpl;
import model.players.strategies.StrategyType;

import java.util.ArrayList;

/**
 * Manages available extension cards and validates extension selections.
 * Separated from controller to keep business logic in the model.
 */
public class ExtensionManager {

    /**
     * Creates and returns all available extension cards.
     */
    public static ArrayList<ExtensionCard> getAvailableExtensions() {
        ArrayList<ExtensionCard> extensions = new ArrayList<>();

        // --- 1. THE SHIELD ---
        // Strategy Player: Activates protection flags.
        // Bot Strategy: CAUTIOUS bot loves this card (value 1000), AGGRESSIVE doesn't care much.
        extensions.add(new ExtensionCard(
                "The Shield", 0,
                "Protects against penalties: your Diamonds and Hearts no longer give negative points.",
                new CardEffect() {
                    @Override
                    public void applyOnVisit(ScoreVisitorImpl visitor) {
                        visitor.setFlag("NO_NEGATIVE_DIAMONDS", true);
                        visitor.setFlag("NO_NEGATIVE_HEARTS", true);
                    }
                },
                (strategy, jest) -> {
                    if (strategy == StrategyType.CAUTIOUS) return 1000; // Absolute priority
                    return 0; // Low value for others
                }
        ));

        // --- 2. THE CROWN ---
        // Strategy Player: Fixed bonus.
        // Bot Strategy: Everyone likes free points (Value 20 > Max value of a normal card).
        extensions.add(new ExtensionCard(
                "The Crown", 0,
                "A royal treasure: Adds 5 points directly to your final score.",
                new CardEffect() {
                    @Override
                    public int calculateBonus(ScoreVisitorImpl visitor) {
                        return 5;
                    }
                },
                (strategy, jest) -> 20
        ));

        // --- 3. THE SPY ---
        // Strategy Player: Face value 2.
        // Bot Strategy: Worth simply its face value (2).
        extensions.add(new ExtensionCard(
                "The Spy", 2,
                "An infiltrated agent: Neutral card worth 2 points.",
                new CardEffect() {}, // Empty effect
                (strategy, jest) -> 2
        ));

        // --- 4. THE JESTER ---
        // Strategy Player: Conditional bonus.
        // Bot Strategy:
        // - CAUTIOUS avoids it (-100).
        // - AGGRESSIVE takes it IF they already have the Joker, otherwise avoids it a bit.
        extensions.add(new ExtensionCard(
                "The Jester", 0,
                "The Joker's friend: Worth +10 if you have the Joker, otherwise -5.",
                new CardEffect() {
                    @Override
                    public int calculateBonus(ScoreVisitorImpl visitor) {
                        return visitor.hasJoker() ? 10 : -5;
                    }
                },
                (strategy, jest) -> {
                    // Check if the bot already has the Joker in their Jest
                    boolean hasJoker = jest.getCards().stream()
                            .anyMatch(c -> c instanceof Joker);

                    if (strategy == StrategyType.CAUTIOUS) return -100; // Too risky

                    // Aggressive attempts it if they have the Joker
                    if (hasJoker) return 50;
                    return -5; // Otherwise it's a penalty
                }
        ));

        return extensions;
    }

    /**
     * Validates if the selected extensions create a valid deck configuration.
     *
     * @param selectedIndices Indices of selected extensions
     * @param playerCount Number of players (3 or 4)
     * @return true if valid, false otherwise
     */
    public static boolean isValidSelection(ArrayList<Integer> selectedIndices, int playerCount) {
        int baseDeckSize = 17; // Standard Jest deck size
        int trophiesCount = (playerCount == 3) ? 2 : 1;
        int extensionsCount = selectedIndices.size();
        int playableDeckSize = baseDeckSize + extensionsCount - trophiesCount;

        // Total rounds must be a whole number (no half-rounds possible)
        float totalRounds = (float) playableDeckSize / playerCount;
        return totalRounds == Math.floor(totalRounds);
    }

    /**
     * Gets a descriptive error message for invalid selection.
     */
    public static String getInvalidSelectionMessage(ArrayList<Integer> selectedIndices, int playerCount) {
        int baseDeckSize = 17;
        int trophiesCount = (playerCount == 3) ? 2 : 1;
        int extensionsCount = selectedIndices.size();
        int playableDeckSize = baseDeckSize + extensionsCount - trophiesCount;
        float totalRounds = (float) playableDeckSize / playerCount;

        return String.format(
                "Invalid configuration: %d players with %d extensions would result in %.2f rounds. " +
                        "The game requires a whole number of rounds. Please select a different combination.",
                playerCount, extensionsCount, totalRounds
        );
    }
}