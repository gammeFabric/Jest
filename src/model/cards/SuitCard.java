package model.cards;

/**
 * Représente une carte à couleur et valeur standard.
 * 
 * <p>Les SuitCard constituent la majorité du deck (16 sur 17 cartes de base).
 * Chaque carte possède une couleur ({@link Suit}) et une valeur ({@link Face}).</p>
 * 
 * <p><b>Composition :</b></p>
 * <ul>
 *   <li>4 couleurs : Cœur, Carreau, Trèfle, Pique</li>
 *   <li>4 valeurs par couleur : As, 2, 3, 4</li>
 *   <li>Total : 16 cartes différentes</li>
 * </ul>
 * 
 * <p><b>Méthodes utilitaires :</b></p>
 * <ul>
 *   <li><code>isAce()</code> - Détecte les As</li>
 *   <li><code>isBlack()</code> - Détecte Trèfle/Pique</li>
 * </ul>
 * 
 * <p><b>Bonus spéciaux :</b></p>
 * <ul>
 *   <li>As solitaire d'une couleur : 5 points au lieu de 1</li>
 *   <li>Paire noire (même valeur en Trèfle et Pique) : +2 points par carte</li>
 * </ul>
 * 
 * @see model.cards.Suit
 * @see model.cards.Face
 */
public class SuitCard extends Card {
    private Suit suit;
    private Face face;


    public SuitCard(boolean isTrophy, Suit suit, Face face) {
        super(isTrophy);
        this.suit = suit;
        this.face = face;
    }

    public Suit getSuit() {
        return suit;
    }

    public Face getFace() {
        return face;
    }

    public boolean isAce() {
        return face == Face.ACE;
    }
    public boolean isBlack() {
        return suit == Suit.CLUBS || suit == Suit.SPADES;
    }

    @Override
    public int getFaceValue() {
        return face.getFaceValue();
    }


    public int getSuitValue(){
        return suit.getStrength();
    }


    @Override
    public String toString() {
        return this.face + " " + this.suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuitCard suitCard = (SuitCard) o;

        return suit == suitCard.suit &&
                face == suitCard.face;
    }

}
