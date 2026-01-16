package model.cards;

import java.io.Serializable;

/**
 * Classe abstraite représentant une carte du jeu.
 * 
 * <p>Cette classe est la base de toutes les cartes du jeu Jest.
 * Elle gère les propriétés communes comme le statut de trophée et
 * les informations associées.</p>
 * 
 * <p><b>Propriétés communes :</b></p>
 * <ul>
 *   <li><code>isTrophy</code> - Indique si la carte est un trophée</li>
 *   <li><code>trophyType</code> - Type de trophée (si applicable)</li>
 *   <li><code>trophySuit/Face</code> - Critère du trophée</li>
 * </ul>
 * 
 * <p><b>Méthodes abstraites :</b></p>
 * <ul>
 *   <li><code>getFaceValue()</code> - Valeur nominale de la carte</li>
 *   <li><code>getSuitValue()</code> - Force de la couleur</li>
 * </ul>
 * 
 * <p><b>Sérialisable</b> pour permettre la sauvegarde des parties.</p>
 * 
 * @see model.cards.SuitCard
 * @see model.cards.Joker
 * @see model.cards.ExtensionCard
 */
public abstract class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    protected boolean isTrophy;
    protected TrophyType trophyType;
    protected Suit trophySuit;
    protected Face trophyFace;

    public Card(boolean isTrophy) {
        this.isTrophy = isTrophy;
        this.trophyType = TrophyType.NONE;
        this.trophyFace = null;
        this.trophySuit = null;
    }

    public boolean isTrophy(){
        return isTrophy;
    }

    public void setTrophy(boolean isTrophy){
        this.isTrophy = isTrophy;
    }

    public TrophyType getTrophyType(){
        return trophyType;
    }

    public void setTrophyType(TrophyType trophy){
        this.trophyType = trophy;
    }

    public void setTrophySuit(Suit suit){
        this.trophySuit = suit;
    }

    public void setTrophyFace(Face trophyFace){
        this.trophyFace = trophyFace;
    }

    public Suit getTrophySuit(){
        return trophySuit;
    }

    public Face getTrophyFace(){
        return trophyFace;
    }

    private boolean hasTrophySuit() {
        return trophySuit != null;
    }
    private boolean hasTrophyFace() {
        return trophyFace != null;
    }

    public String trophyInfo() {
        if (trophyType == null || trophyType == TrophyType.NONE) {
            return "Not a trophy";
        }
        if (hasTrophySuit()) {
            return trophyType + " of " + trophySuit;
        }

        if (hasTrophyFace()) {
            return trophyType + " of " + trophyFace;
        }

        return trophyType.toString();

    }

    public int getCardStrength(){
        int value = 0;
        if (this instanceof SuitCard) {
            value = this.getFaceValue();
            value += this.getSuitValue();
        }
        if (this instanceof Joker){
            value = 4;
            value += 4;
        }

        return value;
    }

    public abstract int getFaceValue();

    public abstract int getSuitValue();
}
