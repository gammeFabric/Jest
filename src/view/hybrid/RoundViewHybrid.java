package view.hybrid;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;
import view.console.RoundView;
import view.gui.RoundViewGUI;
import view.interfaces.IRoundView;

import java.util.ArrayList;

public class RoundViewHybrid implements IRoundView {
    private final RoundView consoleView;
    private final RoundViewGUI guiView;

    public RoundViewHybrid(RoundView consoleView, RoundViewGUI guiView) {
        this.consoleView = consoleView;
        this.guiView = guiView;
    }

    @Override
    public void showRoundStart() {
        consoleView.showRoundStart();
        guiView.showRoundStart();
    }

    @Override
    public void showDealCards() {
        consoleView.showDealCards();
        guiView.showDealCards();
    }

    @Override
    public void showMakeOffers() {
        consoleView.showMakeOffers();
        guiView.showMakeOffers();
    }

    @Override
    public void showDetermineStartingPlayer() {
        consoleView.showDetermineStartingPlayer();
        guiView.showDetermineStartingPlayer();
    }

    @Override
    public void showStartingPlayer(Player p, Card faceUpCard) {
        consoleView.showStartingPlayer(p, faceUpCard);
        guiView.showStartingPlayer(p, faceUpCard);
    }

    @Override
    public void showChoosingPhaseStart() {
        consoleView.showChoosingPhaseStart();
        guiView.showChoosingPhaseStart();
    }

    @Override
    public void showTurn(Player p) {
        consoleView.showTurn(p);
        guiView.showTurn(p);
    }

    @Override
    public void showCardTaken(Player player, Offer takenOffer, Player next) {
        consoleView.showCardTaken(player, takenOffer, next);
        guiView.showCardTaken(player, takenOffer, next);
    }

    @Override
    public void showLastCardTaken(Player player, Offer takenOffer) {
        consoleView.showLastCardTaken(player, takenOffer);
        guiView.showLastCardTaken(player, takenOffer);
    }

    @Override
    public void showDeckEmpty() {
        consoleView.showDeckEmpty();
        guiView.showDeckEmpty();
    }

    @Override
    public void showRoundEnd() {
        consoleView.showRoundEnd();
        guiView.showRoundEnd();
    }

    @Override
    public void showNoOffers() {
        consoleView.showNoOffers();
        guiView.showNoOffers();
    }

    public void showChoosingContext(Player choosingPlayer, ArrayList<Offer> availableOffers) {
        guiView.showChoosingContext(choosingPlayer, availableOffers);
    }

    public void highlightChosenOffer(Offer chosenOffer) {
        guiView.highlightChosenOffer(chosenOffer);
    }

    public void flashChosenOffer(Offer chosenOffer) {
        guiView.flashChosenOffer(chosenOffer);
    }
}

