package model.game;

import model.cards.CardEffect;
import model.cards.ExtensionCard;
import model.cards.Joker;
import model.players.ScoreVisitorImpl;
import model.players.strategies.StrategyType;

import java.util.ArrayList;

/**
 * Gestionnaire des cartes d'extension disponibles.
 * 
 * <p>Cette classe centralise la définition et la validation des cartes
 * d'extension qui peuvent être ajoutées au jeu de base.</p>
 * 
 * <p><b>Cartes d'extension fournies :</b></p>
 * <ul>
 *   <li><b>The Shield</b> - Annule les pénalités de Carreau et Cœur</li>
 *   <li><b>The Crown</b> - Ajoute 5 points directement au score final</li>
 *   <li><b>The Spy</b> - Carte neutre valant 2 points</li>
 *   <li><b>The Jester</b> - +10 avec Joker, -5 sinon</li>
 * </ul>
 * 
 * <p><b>Validation de configuration :</b></p>
 * <ul>
 *   <li>Vérifie que (deck_base + extensions - trophées) / joueurs = entier</li>
 *   <li>Assure un nombre entier de tours pour tous les joueurs</li>
 * </ul>
 * 
 * <p><b>Utilisation :</b></p>
 * <pre>
 *   ArrayList&lt;ExtensionCard&gt; available = ExtensionManager.getAvailableExtensions();
 *   boolean valid = ExtensionManager.isValidSelection(selectedIndices, playerCount);
 * </pre>
 * 
 * @see model.cards.ExtensionCard
 * @see model.cards.CardEffect
 */
public class ExtensionManager {

    public static ArrayList<ExtensionCard> getAvailableExtensions() {
        ArrayList<ExtensionCard> extensions = new ArrayList<>();

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
                    if (strategy == StrategyType.CAUTIOUS)
                        return 1000;
                    return 0;
                }));

        extensions.add(new ExtensionCard(
                "The Crown", 0,
                "A royal treasure: Adds 5 points directly to your final score.",
                new CardEffect() {
                    @Override
                    public int calculateBonus(ScoreVisitorImpl visitor) {
                        return 5;
                    }
                },
                (strategy, jest) -> 20));

        extensions.add(new ExtensionCard(
                "The Spy", 2,
                "An infiltrated agent: Neutral card worth 2 points.",
                new CardEffect() {
                },
                (strategy, jest) -> 2));

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

                    boolean hasJoker = jest.getCards().stream()
                            .anyMatch(c -> c instanceof Joker);

                    if (strategy == StrategyType.CAUTIOUS)
                        return -100;

                    if (hasJoker)
                        return 50;
                    return -5;
                }));

        return extensions;
    }

    public static boolean isValidSelection(ArrayList<Integer> selectedIndices, int playerCount) {
        int baseDeckSize = 17;
        int trophiesCount = (playerCount == 3) ? 2 : 1;
        int extensionsCount = selectedIndices.size();
        int playableDeckSize = baseDeckSize + extensionsCount - trophiesCount;

        float totalRounds = (float) playableDeckSize / playerCount;
        return totalRounds == Math.floor(totalRounds);
    }

    public static String getInvalidSelectionMessage(ArrayList<Integer> selectedIndices, int playerCount) {
        int baseDeckSize = 17;
        int trophiesCount = (playerCount == 3) ? 2 : 1;
        int extensionsCount = selectedIndices.size();
        int playableDeckSize = baseDeckSize + extensionsCount - trophiesCount;
        float totalRounds = (float) playableDeckSize / playerCount;

        return String.format(
                "Invalid configuration: %d players with %d extensions would result in %.2f rounds. " +
                        "The game requires a whole number of rounds. Please select a different combination.",
                playerCount, extensionsCount, totalRounds);
    }
}