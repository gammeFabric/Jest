package core.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private ArrayList<Card> cards;

    public Deck(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(cards, i, j);
        }
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Le deck est vide");
        }
        return cards.remove(cards.size() - 1);
    }

    public Card[] chooseTrophees(int count) {
        if (count > cards.size()) {
            throw new IllegalArgumentException("Pas assez de cartes dans le deck");
        }
        Card[] trophees = new Card[count];
        for (int i = 0; i < count; i++) {
            trophees[i] = drawCard();
        }
        return trophees;
    }

    public int getRemainCount() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
