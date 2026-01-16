/**
 * Package contenant les contrôleurs du jeu.
 * 
 * <p>Ce package implémente la couche contrôleur du pattern MVC, gérant
 * la logique de jeu et la coordination entre le modèle et les vues.</p>
 * 
 * <p><b>Hiérarchie des contrôleurs :</b></p>
 * <ul>
 *   <li>{@link controller.GameController} - Contrôleur principal de la partie</li>
 *   <li>{@link controller.RoundController} - Contrôleur d'un tour standard</li>
 *   <li>{@link controller.FullHandRoundController} - Contrôleur pour variante Full Hand</li>
 *   <li>{@link controller.PlayerController} - Contrôleurs des actions des joueurs</li>
 * </ul>
 * 
 * @see controller.GameController
 */
package controller;