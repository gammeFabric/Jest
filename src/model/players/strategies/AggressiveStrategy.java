package model.players.strategies;

import model.cards.*;
import model.players.Jest;
import model.players.Offer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AggressiveStrategy implements PlayStrategy {
    private Set<String> seenCards; 
    private boolean hasJoker;
    private boolean hasHearts;
    private Jest playerJest;
    private List<Card> trophies; // Ajout : connaissance des trophées

    public AggressiveStrategy() {
        this.seenCards = new HashSet<>();
        this.hasJoker = false;
        this.hasHearts = false;
        this.trophies = new ArrayList<>();
    }

    // Méthode pour permettre au contrôleur de donner l'info des trophées au bot
    public void setTrophies(List<Card> trophies) {
        this.trophies = trophies;
        // On considère les trophées comme des cartes "vues" et inaccessibles
        for (Card c : trophies) {
            seenCards.add(getCardId(c));
        }
    }

    @Override
    public Card[] setCardsToOffer(ArrayList<Card> hand) {
        if (hand.isEmpty()) return null;

        Card bestCard = hand.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Card c : hand) {
            int score = evaluateCardPotential(c);
            if (score > bestScore) {
                bestScore = score;
                bestCard = c;
            }
        }

        Card faceDownCard = bestCard;
        hand.remove(faceDownCard);
        
        Card faceUpCard = hand.get(0);
        hand.remove(faceUpCard);

        return new Card[] { faceUpCard, faceDownCard };
    }

    @Override
    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        if (availableOffers.isEmpty()) return null;

        updateMemory(availableOffers);

        Offer bestOffer = null;
        double maxScore = -9999.0;

        for (Offer offer : availableOffers) {
            Card visible = offer.getFaceUpCard();
            if (visible == null) continue;

            double score = evaluateCardPotential(visible);
            
            // Logique de prise de risque (Bonus pour carte cachée si la visible est moyenne)
            // ... (similaire à avant, simplifié ici pour la clarté) ...

            if (score > maxScore) {
                maxScore = score;
                bestOffer = offer;
            }
        }

        // Sécurité
        if (bestOffer == null) {
            for(Offer o : availableOffers) {
                if (o.getFaceUpCard() != null) {
                    bestOffer = o;
                    break;
                }
            }
        }

        if (bestOffer != null && bestOffer.getFaceUpCard() != null) {
            this.playerJest.addCard(bestOffer.getFaceUpCard());
            bestOffer.setFaceUpCard(null);
        }

        return bestOffer;
    }

    @Override
    public void updateJest(Jest jest) {
        this.playerJest = jest;
        this.hasJoker = false;
        this.hasHearts = false;
        
        for (Card card : jest.getCards()) {
            if (card instanceof SuitCard) {
                if (((SuitCard) card).getSuit() == Suit.HEARTS) hasHearts = true;
            } else if (card instanceof Joker) {
                hasJoker = true;
            }
        }
    }

    private int evaluateCardPotential(Card card) {
        if (card instanceof ExtensionCard) {
            if (playerJest == null) return 0;
            
            return ((ExtensionCard) card).getAIValue(StrategyType.AGGRESSIVE, playerJest);
        }

        // --- 2. LOGIQUE JOKER ---
        if (card instanceof Joker) {
            if (!hasHearts) return 20;
            if (playerJest != null && isGrandSlamPossible()) return 15; 
            return 0; 
        }

        if (card instanceof SuitCard) {
            SuitCard suitCard = (SuitCard) card;
            int val = suitCard.getFaceValue();
            Suit suit = suitCard.getSuit();

            // --- 3. LOGIQUE COEURS ---
            if (suit == Suit.HEARTS) {
                if (hasJoker && hasHearts) {
                    // CORRECTION : Appel sécurisé
                    if (playerJest != null && isGrandSlamPossible()) {
                        return 50; 
                    } else {
                        return -20; 
                    }
                }
                if (hasJoker && !hasHearts) return -100;
                return -5;
            }

            // --- 4. AUTRES COULEURS ---
            if (val == 1) { 
                if (isOnlyOfSuitInJest(suit)) return 6; 
                return 2;
            } 
            
            if ((suit == Suit.CLUBS || suit == Suit.SPADES) && canFormBlackPair(suitCard)) {
                return val + 5; 
            }
            
            if (suit == Suit.DIAMONDS) return -val;
            return val;
        }

        return 0;
    }

    /**
     * Vérifie si l'objectif "Joker + Tous les Cœurs" est encore réalisable.
     */
    private boolean isGrandSlamPossible() {
        // A. Vérifier si on a trop de "déchets" (cartes qui ne sont ni Joker ni Cœur)
        // Dans une partie standard à 3-4 joueurs, on a environ 4 à 6 tours.
        // Si on a déjà récupéré 2 cartes inutiles, il sera dur d'avoir les 5 cartes requises (Joker + 4 Cœurs).
        long trashCards = playerJest.getCards().stream()
                .filter(c -> !(c instanceof Joker) && !isHeart(c))
                .count();
        
        if (trashCards >= 1) return false; // Trop de pollution dans la main

        // B. Vérifier disponibilité des 4 Cœurs (As, 2, 3, 4)
        int[] heartFaces = {1, 2, 3, 4};
        for (int face : heartFaces) {
            String cardId = "HEARTS-" + face;
            
            // 1. Est-ce que je l'ai déjà ?
            boolean iHaveIt = playerJest.getCards().stream().anyMatch(c -> 
                c instanceof SuitCard && ((SuitCard)c).getSuit() == Suit.HEARTS && ((SuitCard)c).getFaceValue() == face
            );
            if (iHaveIt) continue;

            // 2. Si je ne l'ai pas, est-ce qu'il a été vu ailleurs (Adversaire ou Trophée) ?
            // Si 'seenCards' le contient et que je ne l'ai pas, c'est qu'il est inaccessible.
            if (seenCards.contains(cardId)) {
                return false; // Impossible, quelqu'un d'autre l'a ou c'est un trophée.
            }
        }

        return true;
    }

    // --- Méthodes Utilitaires ---

    private boolean isHeart(Card c) {
        return c instanceof SuitCard && ((SuitCard) c).getSuit() == Suit.HEARTS;
    }

    private void updateMemory(ArrayList<Offer> offers) {
        for (Offer o : offers) {
            if (o.getFaceUpCard() != null) {
                seenCards.add(getCardId(o.getFaceUpCard()));
            }
        }
    }

    private String getCardId(Card c) {
        if (c instanceof ExtensionCard) return ((ExtensionCard)c).getName();
        if (c instanceof Joker) return "JOKER";
        SuitCard sc = (SuitCard) c;
        return sc.getSuit().toString() + "-" + sc.getFaceValue();
    }
    
    private boolean canFormBlackPair(SuitCard card) {
        if (playerJest == null) return false; 

        // Cherche la carte complémentaire (Pique <-> Trèfle)
        Suit targetSuit = (card.getSuit() == Suit.SPADES) ? Suit.CLUBS : Suit.SPADES;
        
        for (Card c : playerJest.getCards()) {
            if (c instanceof SuitCard) {
                SuitCard existing = (SuitCard) c;
                if (existing.getSuit() == targetSuit && existing.getFaceValue() == card.getFaceValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOnlyOfSuitInJest(Suit suit) {
        if (playerJest == null) return true; 

        // Vérifie si on n'a AUCUNE carte de cette couleur dans le Jest pour l'instant
        for (Card c : playerJest.getCards()) {
            if (c instanceof SuitCard && ((SuitCard) c).getSuit() == suit) {
                return false; 
            }
        }
        return true; 
    }
}