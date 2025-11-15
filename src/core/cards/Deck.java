package core.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private final ArrayList<Card> cards;
    private ArrayList<Card> trophies = new ArrayList<>();

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
        int trophiesCount = (playerCount == 3) ? 2 : 1;
            for (int i = 0;  i < trophiesCount; i++) {
                Card trophy = dealCard();
                trophy.setTrophy(true);
                trophies.add(trophy);
            }
        assignTrophyType();
        return trophies;
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
            System.out.println("You have added " + card + " to the deck");
            cards.add(card);
        }
    }

    public ArrayList<Card> getTrophies() {
        return trophies;
    }

    public void setTrophies(ArrayList<Card> trophies) {
        this.trophies = trophies;
    }


    // test assigning Trophies
    public void assignTrophyType() {
        for (Card trophy : trophies) {
            if (trophy instanceof Joker){
                trophy.setTrophyType(TrophyType.BEST_JEST);
            }
            if (trophy instanceof SuitCard){
                // Сердечки
                if (((SuitCard) trophy).getSuit() == Suit.HEARTS){
                    trophy.setTrophyType(TrophyType.JOKER);
                }
                // Червы
                else if (((SuitCard) trophy).getSuit() == Suit.CLUBS){
                    if(((SuitCard) trophy).getFaces() != Faces.TWO && ((SuitCard) trophy).getFaces() != Faces.THREE){
                        if (((SuitCard) trophy).getFaces() == Faces.FOUR){
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.SPADES);
//                            type.setSuit(Suit.SPADES);
                            trophy.setTrophyType(type);
                        }
                        else {
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.SPADES);
//                            type.setSuit(Suit.SPADES);
                            trophy.setTrophyType(type);
                        }
                    }
                    else{
                        if (((SuitCard) trophy).getFaces() == Faces.THREE){
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.HEARTS);
//                            type.setSuit(Suit.HEARTS);
                            trophy.setTrophyType(type);
                        }
                        else {
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.HEARTS);
//                            type.setSuit(Suit.HEARTS);
                            trophy.setTrophyType(type);
                        }
                    }
                // Пики
                }
                else if (((SuitCard) trophy).getSuit() == Suit.SPADES){
                    if (((SuitCard) trophy).getFaces() != Faces.THREE && ((SuitCard) trophy).getFaces() != Faces.TWO){
                        if (((SuitCard) trophy).getFaces() == Faces.FOUR){
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.CLUBS);
//                            type.setSuit(Suit.CLUBS);
                            trophy.setTrophyType(type);
                        }
                        else {
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.CLUBS);
//                            type.setSuit(Suit.CLUBS);
                            trophy.setTrophyType(type);
                        }
                    }
                    else{
                        if (((SuitCard) trophy).getFaces() == Faces.THREE){
                            TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                            trophy.setTrophyFace(Faces.TWO);
//                            type.setFace(Faces.TWO);
                            trophy.setTrophyType(type);
                        }
                        else{
                            TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                            trophy.setTrophyFace(Faces.THREE);
//                            type.setFace(Faces.THREE);
                            trophy.setTrophyType(type);
                        }
                    }
                }
                // Бубна
                else{
                    if  (((SuitCard) trophy).getFaces() == Faces.FOUR){
                        trophy.setTrophyType(TrophyType.BEST_JEST_NO_JOKER);
                    }
                    else if (((SuitCard) trophy).getFaces() == Faces.ACE){
                        TrophyType type = TrophyType.MAJORITY_FACE_VALUE;
                        trophy.setTrophyFace(Faces.FOUR);
//                        type.setFace(Faces.FOUR);
                        trophy.setTrophyType(type);
                    }
                    else {
                        if (((SuitCard) trophy).getFaces() == Faces.TWO){
                            TrophyType type = TrophyType.HIGHEST_FACE;
                            trophy.setTrophySuit(Suit.DIAMONDS);
//                            type.setSuit(Suit.DIAMONDS);
                            trophy.setTrophyType(type);
                        }
                        else {
                            TrophyType type = TrophyType.LOWEST_FACE;
                            trophy.setTrophySuit(Suit.DIAMONDS);
//                            type.setSuit(Suit.DIAMONDS);
                            trophy.setTrophyType(type);
                        }
                    }
                }
            }
        }


    }
        // test for cards
    public ArrayList<Card> getCards(){
        return cards;
    }

    public void trophiesInfo(){
        for (Card card : trophies){
            System.out.print(card + ": ");
            System.out.println(card.trophyInfo());
        }
    }




    // functions test
    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.chooseTrophies(3);

        ArrayList<Card> cards = deck.getTrophies();
        for (Card card : cards){
            System.out.print(card + ": ");
            System.out.println(card.trophyInfo());
        }
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
