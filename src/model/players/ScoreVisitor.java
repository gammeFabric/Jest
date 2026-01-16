package model.players;

import model.cards.Card;

/**
 * Interface du pattern Visitor pour le calcul des scores.
 * 
 * <p>Cette interface définit la méthode de visite pour calculer
 * les points associés à chaque carte d'un Jest.</p>
 * 
 * <p><b>Pattern Visitor :</b></p>
 * <ul>
 *   <li>Sépare l'algorithme de calcul de la structure des données</li>
 *   <li>Permet différentes stratégies de scoring (variantes)</li>
 *   <li>Facilite l'ajout de nouvelles règles de score</li>
 * </ul>
 * 
 * <p><b>Implémentations :</b></p>
 * <ul>
 *   <li>{@link ScoreVisitorImpl} - Règles standard</li>
 *   <li>{@link ReverseScoreVisitor} - Scores inversés</li>
 * </ul>
 * 
 * @see model.players.Jest
 * @see model.cards.Card
 */
public interface ScoreVisitor {
    int visit(Card card);
}
