package view.console;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;
import view.interfaces.IRoundView;

public class RoundView implements IRoundView {

    public void showRoundStart() {
        System.out.println("Round Started");
    }

    public void showDealCards() {
        System.out.println("Deal cards to players");
    }

    public void showMakeOffers() {
        System.out.println("Players make offers");
    }

    public void showDetermineStartingPlayer() {
        System.out.println("Determine starting player");
    }

    public void showStartingPlayer(Player p, Card faceUpCard) {
        System.out.println("First to play: " + p.getName()
                + " (face-up card: " + faceUpCard + ")");
    }

    public void showChoosingPhaseStart() {
        System.out.println("\n--- CHOOSING CARDS PHASE ---");
    }

    public void showTurn(Player p) {
        System.out.println("\nIt's " + p.getName() + "'s turn to choose a card.");
    }

    public void showCardTaken(Player player, Offer takenOffer, Player next) {
        System.out.println(player.getName() + " took " + player.getLastCard() +
                " from " + takenOffer.getOwner().getName() +
                " → next player: " + next.getName());
    }

    public void showLastCardTaken(Player player, Offer takenOffer) {
        System.out.println(player.getName() + " took " + player.getLastCard() +
                " → from: " + takenOffer.getOwner().getName());
    }

    public void showDeckEmpty() {
        System.out.println("Deck is empty, finalizing round...");
    }

    public void showRoundEnd() {
        System.out.println("Round has ended");
    }

    public void showNoOffers() {
        System.out.println("No valid offers found. Defaulting to first player.");
    }
}
