package model.cards;

/**
 * Énumération des types de trophées disponibles.
 * 
 * <p>Les trophées sont attribués aux joueurs selon différents critères
 * de performance après la fin de la partie.</p>
 * 
 * <p><b>Types de trophées :</b></p>
 * <ul>
 *   <li><b>HIGHEST_FACE</b> - Plus haute valeur d'une couleur donnée</li>
 *   <li><b>LOWEST_FACE</b> - Plus basse valeur d'une couleur donnée</li>
 *   <li><b>MAJORITY_FACE_VALUE</b> - Plus de cartes d'une valeur donnée</li>
 *   <li><b>JOKER</b> - Possession du Joker</li>
 *   <li><b>BEST_JEST</b> - Meilleur score total</li>
 *   <li><b>BEST_JEST_NO_JOKER</b> - Meilleur score sans Joker</li>
 *   <li><b>NONE</b> - Pas de trophée</li>
 * </ul>
 * 
 * <p><b>Départage des égalités :</b></p>
 * <ul>
 *   <li>Pour HIGHEST/LOWEST_FACE : premier joueur dans l'ordre de jeu</li>
 *   <li>Pour MAJORITY : couleur la plus forte parmi les cartes de cette valeur</li>
 *   <li>Pour BEST_JEST : plus haute valeur de carte</li>
 * </ul>
 * 
 * @see model.game.Game#assignTrophies()
 */
public enum TrophyType {
    NONE("None"),
    HIGHEST_FACE("Highest Face"),
    LOWEST_FACE("Lowest Face"),
    MAJORITY_FACE_VALUE("Majority Face Value"),
    JOKER("Joker"),
    BEST_JEST("Best Jest"),
    BEST_JEST_NO_JOKER("Best Jest without Joker");

    private String name;

    TrophyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;

    }

}
