package view.console;

import model.cards.Card;
import model.players.Offer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanView extends PlayerView {
    private final Scanner scanner;

    public HumanView() {
        this.scanner = new Scanner(System.in);
    }

    // Show hand and ask user to choose face-up and face-down cards
    public int chooseFaceUpCard(String playerName, ArrayList<Card> hand) {
        System.out.println(playerName + " has " + hand.size() + " cards to make an offer");
        System.out.println("These are your cards:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ": " + hand.get(i));
        }

        int faceUpIndex = -1;
        while (faceUpIndex < 0 || faceUpIndex >= hand.size()) {
            System.out.print("Please choose the number of the card to show face-up: ");
            try {
                faceUpIndex = scanner.nextInt() - 1;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear invalid input
                System.out.println("Please enter a valid number.");
            }
        }
        return faceUpIndex;
    }

    // Show available offers and ask user to choose one
    public Offer chooseOffer(String playerName, ArrayList<Offer> selectableOffers) {
//        showMessage(playerName + ", please choose an offer from the available options:");
//        showMessage("These are available offers for " + playerName + ". Choose an offer.");
        showMessage("Choose one from these available offers:");
        for (int i = 0; i < selectableOffers.size(); i++) {
            Offer offer = selectableOffers.get(i);
            System.out.printf("%d) Offer by %s - Face up: %s, Face down: [hidden]%n",
                    i + 1,
                    offer.getOwner().getName(),
                    offer.getFaceUpCard());
        }

        int choice = -1;
        while (choice < 1 || choice > selectableOffers.size()) {
            System.out.print("Choose the number of the offer: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear invalid input
                showMessage("Please enter a valid number.");
            }
        }

        return selectableOffers.get(choice - 1);
    }

    // Ask user which card to take: face-up or face-down
    public boolean chooseFaceUpOrDown() {
        int cardChoice = 0;
        while (cardChoice != 1 && cardChoice != 2) {
            System.out.print("Take 1) Face-up card or 2) Face-down card? ");
            try {
                cardChoice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Please enter 1 or 2.");
            }
        }
        return cardChoice == 1;
    }

    // Show a message
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    public void hasNoEnoughCards(String name) {
        System.out.println(name + "doesn't have enough cards to make an offer");
    }

    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        System.out.println("Thank you. You have chosen " + faceUpCard + " as a faceUp card and " + faceDownCard + " as a faceDown card" );

    }
}
