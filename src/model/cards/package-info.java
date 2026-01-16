/**
 * Package contenant les classes relatives aux cartes.
 * 
 * <p>Ce package implémente la hiérarchie des cartes du jeu Jest, incluant
 * les cartes standards, le Joker et les cartes d'extension.</p>
 * 
 * <p><b>Hiérarchie des cartes :</b></p>
 * <ul>
 *   <li>{@link model.cards.Card} - Classe abstraite de base</li>
 *   <li>{@link model.cards.SuitCard} - Cartes avec couleur et valeur</li>
 *   <li>{@link model.cards.Joker} - Carte spéciale Joker</li>
 *   <li>{@link model.cards.ExtensionCard} - Cartes additionnelles avec effets</li>
 * </ul>
 * 
 * @see model.cards.Card
 * @see model.cards.Deck
 */
package model.cards;