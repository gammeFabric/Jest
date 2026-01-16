package model.game;

import model.players.ScoreVisitor;
import java.io.Serializable;

/**
 * Interface définissant une variante de jeu.
 * 
 * <p>Cette interface permet d'implémenter différentes règles et mécaniques
 * de jeu tout en conservant la structure de base de Jest.</p>
 * 
 * <p><b>Méthodes requises :</b></p>
 * <ul>
 *   <li><code>getName()</code> - Nom de la variante</li>
 *   <li><code>setup(Game)</code> - Configuration initiale</li>
 *   <li><code>getRulesDescription()</code> - Description des règles</li>
 *   <li><code>createScoreVisitor()</code> - Visiteur de score personnalisé</li>
 *   <li><code>getMinPlayers()</code> - Nombre minimum de joueurs</li>
 *   <li><code>getMaxPlayers()</code> - Nombre maximum de joueurs</li>
 * </ul>
 * 
 * <p><b>Variantes implémentées :</b></p>
 * <ul>
 *   <li>{@link model.game.variants.StandardVariant} - Règles classiques</li>
 *   <li>{@link model.game.variants.ReverseScoringVariant} - Scores inversés</li>
 *   <li>{@link model.game.variants.FullHandVariant} - Distribution complète</li>
 * </ul>
 * 
 * <p><b>Sérialisable</b> pour la sauvegarde de parties.</p>
 * 
 * @see model.game.variants
 */
public interface GameVariant extends Serializable {
    
    String getName();

    
    void setup(Game game);

    
    String getRulesDescription();

    
    ScoreVisitor createScoreVisitor();

    
    int getMinPlayers();

    
    int getMaxPlayers();
}
