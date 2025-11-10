package core.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private final ArrayList<Card> cards;

    public Deck(ArrayList<Card> cards) {
        this.cards = cards;
        deckInit();
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(cards, i, j);
        }
    }

    // Initialization of our deck (we add all 17 cards including 1 Joker)
    private void deckInit(){
        cards.clear();
        for  (Suit suit : Suit.values()) {
            for (Faces face : Faces.values()) {
                cards.add(new SuitCard(false, suit, face));
            }
        }
        cards.add(new Joker(false));
        shuffle();
    }
    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Le deck est vide");
        }
        return cards.removeLast();
    }

    public Card[] chooseTrophees(int count) {
        if (count > cards.size()) {
            throw new IllegalArgumentException("Pas assez de cartes dans le deck");
        }
        Card[] trophees = new Card[count];
        for (int i = 0; i < count; i++) {
            trophees[i] = dealCard();
        }
        return trophees;
    }

    public ArrayList<Card> chooseTrophies(int playerCount) {
        ArrayList<Card> trophies = new ArrayList<>();
        int trophiesCount = (playerCount == 3) ? 2 : 1;
            for (int i = 0;  i < trophiesCount; i++) {
                Card trophy = dealCard();
                trophy.setTrophy(true);
                trophies.add(trophy);
            }
        return trophies;
    }

    public int getRemainingCount() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }





    // functions test
    public static void main(String[] args) {
        Deck deck = new Deck(new ArrayList<>());
        System.out.println("Deck has " + deck.getRemainingCount() + " cards.");

        ArrayList<Card> trophies = deck.chooseTrophies(3);
        System.out.println("Chosen trophies:");
        trophies.forEach(System.out::println);
        System.out.println("\n");
        for (Card card : deck.cards) {
            System.out.println(card);
        }

        Card card = deck.dealCard();
        System.out.println("Card given: " + card);
        System.out.println("Cards left in the deck: " + deck.getRemainingCount());
    }

}
