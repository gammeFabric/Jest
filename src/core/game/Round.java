package core.game;

import core.cards.Deck;
import core.players.Offer;
import core.players.Player;


import java.util.ArrayList;
import java.util.Comparator;

public class Round {
    private ArrayList<Offer> offers;
    private ArrayList<Player> players;
    private Player currentPlayer;
    private boolean isOver;
    private Deck deck;


    public Round(ArrayList<Player> players,  Deck deck) {
        this.players = players;
        this.deck = deck;
        this.offers = new ArrayList<>();
    }

    public ArrayList<Offer> getAvailableOffers() {
        ArrayList<Offer> availableOffers = new ArrayList<>();
        for (Offer offer : offers) {
            if (offer.isComplete()) {
                availableOffers.add(offer);
            }
        }
        return availableOffers;
    }

    public void StartRound(){
        System.out.println("Round Started");

        System.out.println("Deal cards to players");
        dealCards();

        System.out.println("Players make offers");
        makeOffers();

        System.out.println("Determine starting player");
        determineStartingPlayer();





    }

    public void dealCards() {
        for (Player player : players) {
            player.addToHand(deck.dealCard());
            player.addToHand(deck.dealCard());
        }
    }

    public void makeOffers() {
        for (Player player : players) {
            Offer offer = player.makeOffer();
            offers.add(offer);
        }
    }

    public Player determineStartingPlayer(){
        Offer bestOffer = offers.stream()
                .filter(offer -> offer.getFaceUpCard() != null)
                .max(Comparator
                        .comparingInt((Offer offer) -> offer.getFaceUpCard().getFaceValue()) // value comparator
                        .thenComparingInt(offer -> offer.getFaceUpCard().getSuitValue()) // tie-breaking by Suit
                )
                .orElse(null);

        if (bestOffer == null) {
            System.out.println("No valid offers found. Defaulting to first player.");
            return players.getFirst();
        }

        Player startingPlayer = bestOffer.getOwner();
        System.out.println("First to play: " + startingPlayer.getName() +
                " (face-up card: " + bestOffer.getFaceUpCard() + ")");
        return startingPlayer;
    }

    public void playChoosingPhase(Player startingPlayer){
        System.out.println("\n--- CHOOSING CARDS PHASE ---");

        Player currentPlayer = startingPlayer;
        int turns = 0;
        int maxTurns = players.size();
        while (turns < maxTurns) {
            System.out.println("\nIt's " + currentPlayer.getName() + "'s turn to choose a card.");
            currentPlayer.chooseCard(getAvailableOffers());
            Player nextPlayer = currentPlayer.getNextPlayer();

            // need to realise what to do if nextPlayer == null
            //if (nextPlayer == null) {
            //    nextPlayer = players.get(0);
            //}

            System.out.println(currentPlayer.getName() + " took " + currentPlayer.getLastCard() +
                    " → next player: " + nextPlayer.getName());
            currentPlayer = nextPlayer;
            turns++;
        }
    }


//    public void nextTurn();
//    public boolean isRoundComplete();
//    public Player getCurrentPlayer() {}
//    public Player getNextPlayer(Player currentPlayer, Player takenFrom);
//    public void endRound();
//    public void dealCards(Deck deck, ArrayList<Player> players);


}
