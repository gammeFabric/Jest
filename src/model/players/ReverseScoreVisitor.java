package model.players;

/**
 * Visiteur de score pour la variante "Reverse Scoring".
 * 
 * <p>Ce visiteur étend {@link ScoreVisitorImpl} en inversant simplement
 * le score total final.</p>
 * 
 * <p><b>Fonctionnement :</b></p>
 * <ul>
 *   <li>Calcul normal des scores via le visiteur parent</li>
 *   <li>Inversion du total dans <code>getTotalScore()</code></li>
 *   <li>Pas de modification des règles individuelles</li>
 * </ul>
 * 
 * <p><b>Utilisation :</b></p>
 * <pre>
 * ScoreVisitor visitor = new ReverseScoreVisitor();
 * player.calculateScore(visitor);
 * // Score positif devient négatif et vice-versa
 * </pre>
 * 
 * @see model.players.ScoreVisitorImpl
 * @see model.game.variants.ReverseScoringVariant
 */
public class ReverseScoreVisitor extends ScoreVisitorImpl {

    @Override
    public int getTotalScore() {

        return -super.getTotalScore();
    }
}