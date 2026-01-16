package model.game.variants;

import model.game.Game;
import model.game.GameVariant;
import model.players.ScoreVisitor;
import model.players.ScoreVisitorImpl;
import model.players.Player;

import java.util.ArrayList;

/**
 * Variante "Full Hand" - Distribution complète des cartes.
 * 
 * <p>Dans cette variante, toutes les cartes du deck sont distribuées
 * équitablement au début. Les joueurs créent des offres à partir de
 * leur main complète jusqu'à n'avoir qu'une carte restante.</p>
 * 
 * <p><b>Modifications :</b></p>
 * <ul>
 *   <li>Distribution initiale de toutes les cartes (répartition équitable)</li>
 *   <li>Offres créées depuis toute la main au lieu de 2 cartes</li>
 *   <li>Plusieurs tours successifs d'offres/choix</li>
 *   <li>Fin quand tous les joueurs ont ≤1 carte</li>
 *   <li>Dernière carte automatiquement ajoutée au Jest</li>
 * </ul>
 * 
 * <p><b>Règles de score :</b> Identiques à la variante standard</p>
 * 
 * <p><b>Joueurs :</b> 3-4 joueurs</p>
 * 
 * @see model.game.FullHandRound
 * @see model.game.GameVariant
 */
public class FullHandVariant implements GameVariant {

    private static final String NAME = "Full Hand";
    private static final String DESCRIPTION = "Full Hand variant rules:\n" +
            "- All cards are distributed fairly at the beginning\n" +
            "- Each turn, players choose 2 cards from their entire hand\n" +
            "- Offers are made following classic rules\n" +
            "- Game continues until each player has only 1 card left\n" +
            "- The last card of each player automatically joins their Jest\n" +
            "- Uses standard Jest scoring rules";

    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setup(Game game) {

    }

    @Override
    public String getRulesDescription() {
        return DESCRIPTION;
    }

    @Override
    public ScoreVisitor createScoreVisitor() {

        return new ScoreVisitorImpl();
    }

    @Override
    public int getMinPlayers() {
        return MIN_PLAYERS;
    }

    @Override
    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    public static int calculateCardsPerPlayer(int playerCount, int totalCards) {
        return totalCards / playerCount;
    }

    public static boolean shouldEndGame(ArrayList<Player> players) {
        for (Player player : players) {
            if (player.getHand().size() > 1) {
                return false;
            }
        }
        return true;
    }
}
