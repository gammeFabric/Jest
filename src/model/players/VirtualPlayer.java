package model.players;

import model.cards.Card;
import model.players.strategies.*;

import java.util.ArrayList;

/**
 * Représente un joueur virtuel (IA).
 * 
 * <p>Les joueurs virtuels utilisent une stratégie de jeu ({@link PlayStrategy})
 * pour prendre leurs décisions automatiquement.</p>
 * 
 * <p><b>Stratégies disponibles :</b></p>
 * <ul>
 *   <li>{@link model.players.strategies.RandomStrategy} - Choix aléatoires</li>
 *   <li>{@link model.players.strategies.AggressiveStrategy} - Maximisation des points</li>
 *   <li>{@link model.players.strategies.CautiousStrategy} - Évitement des risques</li>
 * </ul>
 * 
 * <p><b>Fonctionnement :</b></p>
 * <ul>
 *   <li>Délégation des décisions à la stratégie</li>
 *   <li>Mise à jour de la stratégie avec le Jest actuel</li>
 *   <li>Aucune interaction utilisateur requise</li>
 * </ul>
 * 
 * <p><b>Filtrage des offres :</b> Le joueur virtuel ne peut pas
 * choisir sa propre offre sauf en dernier recours.</p>
 * 
 * @see model.players.Player
 * @see model.players.strategies.PlayStrategy
 */
public class VirtualPlayer extends Player {
    private PlayStrategy strategy;
    private StrategyType strategyType;

    public VirtualPlayer(String name, StrategyType type) {
        super(name, true);
        setStrategy(type);
    }

    public void setStrategy(StrategyType type) {
        this.strategyType = type;
        switch (type) {
            case RANDOM -> strategy = new RandomStrategy();
            case AGGRESSIVE -> strategy = new AggressiveStrategy();
            case CAUTIOUS -> strategy = new CautiousStrategy();
            default -> strategy = new RandomStrategy();
        }
    }

    public StrategyType getStrategy() {
        return strategyType;
    }

    @Override
    public Offer makeOffer(int faceUpIndex, int faceDownIndex) {
        throw new UnsupportedOperationException("Controller must call makeOffer().");
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers, Offer chosenOffer, boolean isFaceUp) {
        throw new UnsupportedOperationException("Controller must call chooseCard(availableOffers).");
    }

    @Override
    public Offer makeOffer() {

        Card[] offeredCards = strategy.setCardsToOffer(this.hand);

        if (offeredCards == null || offeredCards.length < 2) {
            System.err.println("Erreur: La stratégie n'a pas retourné assez de cartes pour l'offre.");
            return null;
        }

        Card faceUpCard = offeredCards[0];
        Card faceDownCard = offeredCards[1];

        Offer newOffer = new Offer(this, faceUpCard, faceDownCard);

        this.offer = newOffer;

        this.hand.remove(faceUpCard);
        this.hand.remove(faceDownCard);

        return newOffer;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {

        strategy.updateJest(this.jest);

        ArrayList<Offer> validOffers = new ArrayList<>();
        for (Offer o : availableOffers) {

            if (o.getOwner() != this) {
                validOffers.add(o);
            }
        }

        if (validOffers.isEmpty()) {

            return strategy.chooseCard(availableOffers);
        } else {

            return strategy.chooseCard(validOffers);
        }
    }
}
