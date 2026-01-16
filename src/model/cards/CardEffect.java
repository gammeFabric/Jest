package model.cards;

import model.players.ScoreVisitorImpl;

/**
 * Interface définissant les effets des cartes d'extension.
 * 
 * <p>Cette interface permet d'implémenter des effets personnalisés
 * pour les cartes d'extension, modifiant le calcul des scores.</p>
 * 
 * <p><b>Types d'effets :</b></p>
 * <ul>
 *   <li><code>applyOnVisit()</code> - Applique des flags/modifications au visiteur de score</li>
 *   <li><code>calculateBonus()</code> - Calcule un bonus de points additionnel</li>
 * </ul>
 * 
 * <p><b>Exemples d'effets :</b></p>
 * <ul>
 *   <li>Annulation des pénalités de certaines couleurs</li>
 *   <li>Bonus conditionnel basé sur d'autres cartes</li>
 *   <li>Points fixes additionnels</li>
 * </ul>
 * 
 * @see model.cards.ExtensionCard
 * @see model.players.ScoreVisitorImpl
 */
public interface CardEffect {
    
    default void applyOnVisit(ScoreVisitorImpl visitor) {}

    
    default int calculateBonus(ScoreVisitorImpl visitor) { 
        return 0; 
    }
}