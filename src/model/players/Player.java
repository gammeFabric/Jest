package model.players;

import view.console.PlayerView;
import model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe abstraite représentant un joueur.
 * 
 * <p>Cette classe est la base commune pour les joueurs humains et virtuels,
 * gérant les propriétés et comportements communs.</p>
 * 
 * <p><b>Propriétés communes :</b></p>
 * <ul>
 *   <li><code>name</code> - Nom du joueur</li>
 *   <li><code>hand</code> - Main actuelle (cartes non jouées)</li>
 *   <li><code>jest</code> - Collection de cartes gagnées</li>
 *   <li><code>offer</code> - Offre actuelle du tour</li>
 *   <li><code>isVirtual</code> - Indique si IA ou humain</li>
 *   <li><code>score</code> - Score calculé en fin de partie</li>
 * </ul>
 * 
 * <p><b>Méthodes abstraites :</b></p>
 * <ul>
 *   <li><code>makeOffer()</code> - Créer une offre</li>
 *   <li><code>chooseCard()</code> - Choisir une carte parmi les offres</li>
 * </ul>
 * 
 * <p><b>Calcul de score :</b></p>
 * <pre>
 * player.calculateScore(scoreVisitor);
 * int finalScore = player.getScore();
 * </pre>
 * 
 * <p><b>Sérialisable</b>, sauf la vue (transient).</p>
 * 
 * @see model.players.HumanPlayer
 * @see model.players.VirtualPlayer
 */
public abstract class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected ArrayList<Card> hand;
    protected Jest jest;
    protected Offer offer;
    protected boolean isVirtual;
    protected int score;

    public transient PlayerView view;

    public Player(String name, boolean isVirtual) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.jest = new Jest();
        this.offer = null;
        this.isVirtual = isVirtual;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public Jest getJest() {
        return jest;
    }

    public Offer getOffer() {
        return offer;
    }

    public int getScore() {
        return score;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Card getLastCard() {

        return this.jest.getCards().getLast();
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addToHand(Card card) {
        this.hand.add(card);
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void takeRemainingOfferCard() {
        Card faceUp = offer.getFaceUpCard();
        Card faceDown = offer.getFaceDownCard();

        if (faceUp != null) {
            jest.addCard(faceUp);
            offer.setFaceUpCard(null);
        }

        if (faceDown != null) {
            jest.addCard(faceDown);
            offer.setFaceDownCard(null);
        }
    }

    public void calculateScore(ScoreVisitor visitor) {
        jest.accept(visitor);
        if (visitor instanceof ScoreVisitorImpl) {
            score = ((ScoreVisitorImpl) visitor).getTotalScore();
        }
    }

    public abstract Offer makeOffer(int faceUpIndex, int faceDownIndex);

    public abstract Offer chooseCard(ArrayList<Offer> availableOffers, Offer chosenOffer, boolean isFaceUp);

    public abstract Offer makeOffer();

    public abstract Offer chooseCard(ArrayList<Offer> availableOffers);
}
