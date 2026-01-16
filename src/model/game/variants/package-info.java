/**
 * Package contenant les implémentations des variantes de jeu.
 * 
 * <p>Chaque variante modifie certaines règles du jeu de base tout en
 * conservant la mécanique principale de Jest.</p>
 * 
 * <p><b>Variantes disponibles :</b></p>
 * <ul>
 *   <li>{@link StandardVariant} - Règles classiques du Jest</li>
 *   <li>{@link ReverseScoringVariant} - Scores positifs/négatifs inversés</li>
 *   <li>{@link FullHandVariant} - Distribution complète des cartes</li>
 * </ul>
 * 
 * @see model.game.GameVariant
 */
package model.game.variants;