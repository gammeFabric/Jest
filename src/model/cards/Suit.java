package model.cards;

/**
 * Énumération représentant les couleurs des cartes.
 * 
 * <p>Chaque couleur possède une force pour le départage et des
 * règles de score spécifiques.</p>
 * 
 * <p><b>Couleurs et forces :</b></p>
 * <ul>
 *   <li>HEARTS (Cœur) - Force 1 - Score spécial selon Joker</li>
 *   <li>DIAMONDS (Carreau) - Force 2 - Points négatifs</li>
 *   <li>CLUBS (Trèfle) - Force 3 - Points positifs</li>
 *   <li>SPADES (Pique) - Force 4 - Points positifs</li>
 * </ul>
 * 
 * <p><b>Règles de score :</b></p>
 * <ul>
 *   <li><b>Trèfle/Pique</b> : +valeur nominale</li>
 *   <li><b>Carreau</b> : -valeur nominale</li>
 *   <li><b>Cœur</b> : 0, +valeur (avec Joker et 4 Cœurs), ou -valeur (avec Joker et 1-3 Cœurs)</li>
 * </ul>
 * 
 * @see model.cards.SuitCard
 */
public enum Suit {
    HEARTS(1),
    DIAMONDS(2),
    CLUBS(3),
    SPADES(4);

    private final int strength;
    Suit(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }
}
