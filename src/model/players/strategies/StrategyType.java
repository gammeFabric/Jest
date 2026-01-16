package model.players.strategies;

/**
 * Énumération des stratégies de jeu disponibles pour les IA.
 * 
 * <p>Chaque stratégie représente une approche différente pour prendre
 * les décisions de jeu au nom d'un joueur virtuel.</p>
 * 
 * <p><b>Stratégies disponibles :</b></p>
 * <ul>
 *   <li><b>RANDOM</b> - Choix aléatoires</li>
 *   <li><b>AGGRESSIVE</b> - Maximisation du score</li>
 *   <li><b>CAUTIOUS</b> - Minimisation des risques</li>
 * </ul>
 */
public enum StrategyType {
    RANDOM,
    AGGRESSIVE,
    CAUTIOUS
}
