package model.cards;

 import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        deckInit();
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(cards, i, j);
        }
    }

    
    private void deckInit(){
        cards.clear();
        for  (Suit suit : Suit.values()) {
            for (Face face : Face.values()) {
                cards.add(new SuitCard(false, suit, face));
            }
        }
        cards.add(new Joker(false));
        shuffle();
    }

    
    public void addExtensions(ArrayList<ExtensionCard> selectedExtensions) {
        if (selectedExtensions == null || selectedExtensions.isEmpty()) {
            return;
        }

        
        this.cards.addAll(selectedExtensions);
        
        
        shuffle();
        
        System.out.println(selectedExtensions.size() + " cartes d'extension ajoutées au paquet.");
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Le deck est vide");
        }
        return cards.removeLast();
    }

    public int getRemainingCount() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void addCard(Card card) {
        if (cards.contains(card))
            System.out.println("You cannot add the same card in the deck");
        else if(card == null)
            System.out.println("You cannot add null card");
        else{
            cards.add(card);
        }
    }

    
    public ArrayList<Card> getCards(){
        return cards;
    }
}