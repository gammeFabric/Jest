package model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
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
//            System.out.println("You have added " + card + " to the deck");
            cards.add(card);
        }
    }



        // test for cards
    public ArrayList<Card> getCards(){
        return cards;
    }




    // functions test
    public static void main(String[] args) {
        Deck deck = new Deck();
//        deck.chooseTrophies(3);

//        ArrayList<Card> cards = deck.getTrophies();
//        for (Card card : cards){
//            System.out.print(card + ": ");
//            System.out.println(card.trophyInfo());
//        }
//        System.out.println("Deck has " + deck.getRemainingCount() + " cards.");
//
//        ArrayList<Card> trophies = deck.chooseTrophies(3);
//        System.out.println("Chosen trophies:");
//        trophies.forEach(System.out::println);
//        System.out.println("\n");
//        for (Card card : deck.cards) {
//            System.out.println(card);
//        }
//
//        Card card = deck.dealCard();
//        System.out.println("Card given: " + card);
//        System.out.println("Cards left in the deck: " + deck.getRemainingCount());
//        Card card1 =  new SuitCard(false, Suit.HEARTS, Faces.ACE);
//        deck.addCard(card1);
    }


}
