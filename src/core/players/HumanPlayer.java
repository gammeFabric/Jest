package core.players;

import core.cards.Card;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner scanner = new Scanner(System.in);
    public HumanPlayer(String name, boolean isVirtual) {
        super(name, isVirtual);
    }

    public Offer makeOffer(){
        if (hand.size() < 2) {
            System.out.println(this.name + "doesn't have enough cards to make an offer");
            return null;
        }

        System.out.println(this.name + " has " + hand.size() + " cards to make an offer");
        System.out.println("These are your cards: ");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i+1) + ": " + hand.get(i));
        }
        int faceUpIndex = -1;
        while (faceUpIndex < 0 || faceUpIndex >= hand.size()) {
            System.out.print("Please choose the number of the card to show face-up: ");
            faceUpIndex = scanner.nextInt() - 1;
        }
        int faceDownIndex = (faceUpIndex == 0) ? 1 : 0;
        Card faceUpCard = hand.get(faceUpIndex);
        Card faceDownCard = hand.get(faceDownIndex);
        System.out.println("Thank you. You have chosen " + hand.get(faceUpIndex) + " as a faceUp card and " + hand.get(faceDownIndex) + " as a faceDown card" );

        Offer playerOffer = new Offer(this, faceUpCard, faceDownCard);
        offer = playerOffer;
        hand.clear();
        return playerOffer;
    }
//
//    public Card chooseCard(ArrayList<Offer> availableOffers) {
//        System.out.println(name + ", please choose a card from other players' offers: ");
//        System.out.println("Available offers (you can't take from your own offer): ");
//
//
//        // We get all the complete offer including a player's offer who chooses a card
//        ArrayList<Offer> completeOffers = new ArrayList<>();
//        for (Offer offer : availableOffers) {
//            if (offer != null && offer.isComplete()) {
//                completeOffers.add(offer);
//            }
//        }
//
//        // Check if we have available offer
//        if (completeOffers.isEmpty()) {
//            System.out.println("No available offers to choose from.");
//            return null;
//        }
//
//        // We make a list with selectableOffers
//        ArrayList<Offer> selectableOffers = new ArrayList<>();
//        for (Offer offer : completeOffers) {
//            if (offer.getOwner() != this) {
//                selectableOffers.add(offer);
//            }
//        }
//
//        // Check if no others offers - so you have to take from your own offer
//        if (selectableOffers.isEmpty()) {
//            for (Offer offer : completeOffers) {
//                if (offer.getOwner() == this) {
//                    selectableOffers.add(offer);
//                    System.out.println("You are the last player — you must take a card from your own offer!");
//                    break;
//                }
//            }
//        }
//
//        // Bug when we don't have any offer available to select from
//        if  (selectableOffers.isEmpty()) {
//            System.out.println("No available offers to choose from.");
//            return null;
//        }
//
//        // Available offers print info
//        System.out.println("Available offers:");
//        for (int i = 0; i < selectableOffers.size(); i++) {
//            Offer offer = selectableOffers.get(i);
//            System.out.printf("%d) Offer by %s - Face up: %s, Face down: [hidden]%n",
//                    i + 1,
//                    offer.getOwner().getName(),
//                    offer.getFaceUpCard());
//        }
//
//        // User makes a choice of an offer
//
//        int choice = -1;
//
//        while (choice < 0 || choice > selectableOffers.size()) {
//            System.out.print("Please choose the number of the offer to choose from: ");
//            try {
//                choice = scanner.nextInt();
//            } catch (InputMismatchException e) {
//                scanner.nextLine();
//                System.out.println("Please enter a valid number");
//            }
//        }
//
//        Offer chosenOffer = selectableOffers.get(choice - 1);
//        Player chosenOwner = chosenOffer.getOwner();
//        this.nextPlayer = chosenOwner;
//
//
//        // User makes a choice of a card
//
//        int cardChoice = 0;
//        Card takenCard = null;
//        boolean isFaceUp = false;
//
//        while (cardChoice != 1 && cardChoice != 2){
//            System.out.print("Take 1) Face-up card or 2) Face-down card?");
//            try{
//                cardChoice = scanner.nextInt();
//            } catch (InputMismatchException e) {
//                scanner.nextLine();
//                System.out.println("Please enter 1 or 2.");
//            }
//        }
//
//        if (cardChoice == 1) {
//            isFaceUp = true;
//        }
//
//        takenCard = (cardChoice == 1) ? chosenOffer.getFaceUpCard() : chosenOffer.getFaceDownCard();
//
//        if (takenCard == null) {
//            System.out.println("No such card available.");
//            return null;
//        }
//
//        chosenOffer.takeCard(isFaceUp);
//        this.jest.addCard(takenCard);
//
//        return takenCard;
//
//    }

    public Offer chooseCard(ArrayList<Offer> availableOffers) {
        System.out.println(name + ", please choose a card from other players' offers: ");
        System.out.println("Available offers (you can't take from your own offer): ");


        // We get all the complete offer including a player's offer who chooses a card
        ArrayList<Offer> completeOffers = new ArrayList<>();
        for (Offer offer : availableOffers) {
            if (offer != null && offer.isComplete()) {
                completeOffers.add(offer);
            }
        }

        // Check if we have available offer
        if (completeOffers.isEmpty()) {
            System.out.println("No available offers to choose from.");
        }

        // We make a list with selectableOffers
        ArrayList<Offer> selectableOffers = new ArrayList<>();
        for (Offer offer : completeOffers) {
            if (offer.getOwner() != this) {
                selectableOffers.add(offer);
            }
        }

        // Check if no others offers - so you have to take from your own offer
        if (selectableOffers.isEmpty()) {
            for (Offer offer : completeOffers) {
                if (offer.getOwner() == this) {
                    selectableOffers.add(offer);
                    System.out.println("You are the last player — you must take a card from your own offer!");
                    break;
                }
            }
        }

        // Bug when we don't have any offer available to select from
        if (selectableOffers.isEmpty()) {
            System.out.println("No available offers to choose from.");
        }

        // Available offers print info
        System.out.println("Available offers:");
        for (int i = 0; i < selectableOffers.size(); i++) {
            Offer offer = selectableOffers.get(i);
            System.out.printf("%d) Offer by %s - Face up: %s, Face down: [hidden]%n",
                    i + 1,
                    offer.getOwner().getName(),
                    offer.getFaceUpCard());
        }

        // User makes a choice of an offer

        int choice = -1;

        while (choice < 0 || choice > selectableOffers.size()) {
            System.out.print("Please choose the number of the offer to choose from: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Please enter a valid number");
            }
        }

        Offer chosenOffer = selectableOffers.get(choice - 1);


        // User makes a choice of a card

        int cardChoice = 0;
        Card takenCard = null;
        boolean isFaceUp = false;

        while (cardChoice != 1 && cardChoice != 2) {
            System.out.print("Take 1) Face-up card or 2) Face-down card?");
            try {
                cardChoice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Please enter 1 or 2.");
            }
        }

        if (cardChoice == 1) {
            isFaceUp = true;
        }

        takenCard = (cardChoice == 1) ? chosenOffer.getFaceUpCard() : chosenOffer.getFaceDownCard();

        if (takenCard == null) {
            System.out.println("No such card available.");
        }

        chosenOffer.takeCard(isFaceUp);
        this.jest.addCard(takenCard);
        return chosenOffer;
    }
    }
