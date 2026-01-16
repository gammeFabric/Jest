package model.cards;

/**
 * Énumération représentant les valeurs des cartes à couleur.
 * 
 * <p>Dans Jest, chaque couleur possède 4 valeurs (As à 4).</p>
 * 
 * <p><b>Valeurs disponibles :</b></p>
 * <ul>
 *   <li>ACE (As) - Valeur 1</li>
 *   <li>TWO (2) - Valeur 2</li>
 *   <li>THREE (3) - Valeur 3</li>
 *   <li>FOUR (4) - Valeur 4</li>
 * </ul>
 * 
 * <p><b>Règles spéciales :</b></p>
 * <ul>
 *   <li>Un As seul d'une couleur vaut 5 points</li>
 *   <li>Les valeurs participent au calcul de force pour départager</li>
 * </ul>
 * 
 * @see model.cards.SuitCard
 */
public enum Face {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4);

    private final int faceValue;

    Face(int faceValue) {
        this.faceValue = faceValue;
    }

    public int getFaceValue() {
        return faceValue;
    }
}
