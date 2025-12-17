package model.players;

import model.cards.Card;
import model.players.strategies.*;

import java.util.ArrayList;

public class VirtualPlayer extends Player {
    private PlayStrategy strategy;

    public VirtualPlayer(String name, StrategyType type) {
        super(name, true);
        setStrategy(type);
    }

    public void setStrategy(StrategyType type) {
        switch (type) {
            case RANDOM -> strategy = new RandomStrategy();
            case AGGRESSIVE -> strategy = new AggressiveStrategy();
            case CAUTIOUS -> strategy = new CautiousStrategy();
            default -> strategy = new RandomStrategy();
        }
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
        // 1. La stratégie décide quelles cartes choisir dans la main
        Card[] offeredCards = strategy.setCardsToOffer(this.hand);
        
        // Sécurité contre les erreurs potentielles de stratégie
        if (offeredCards == null || offeredCards.length < 2) {
            System.err.println("Erreur: La stratégie n'a pas retourné assez de cartes pour l'offre.");
            return null;
        }

        Card faceUpCard = offeredCards[0];
        Card faceDownCard = offeredCards[1];

        // 2. Création de l'offre
        Offer newOffer = new Offer(this, faceUpCard, faceDownCard);
        
        // 3. IMPORTANT : On sauvegarde l'offre dans l'état du joueur
        // Sans cette ligne, `player.getOffer()` renvoie null plus tard dans le Round
        this.offer = newOffer; 
        
        return newOffer;
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        // Mise à jour du contexte pour la stratégie
        strategy.updateJest(this.jest);

        // --- CORRECTION : FILTRAGE DES OFFRES LÉGALES ---
        
        // 1. On crée une liste contenant uniquement les offres des ADVERSAIRES
        ArrayList<Offer> validOffers = new ArrayList<>();
        for (Offer o : availableOffers) {
            // On vérifie que le propriétaire de l'offre n'est pas nous-même
            if (o.getOwner() != this) {
                validOffers.add(o);
            }
        }

        // 2. Application de la règle du "Dernier Joueur"
        // Si la liste filtrée est vide, cela signifie qu'il ne reste que notre propre offre
        // (ou aucune, mais le jeu s'arrête avant).
        // Dans ce cas UNIQUE (fin de tour), on a le droit/devoir de prendre chez soi. 
        if (validOffers.isEmpty()) {
             // On passe la liste complète (qui contient notre offre)
             return strategy.chooseCard(availableOffers);
        } else {
             // Sinon, on oblige la stratégie à choisir parmi les offres adverses
             return strategy.chooseCard(validOffers);
        }
    }
}
