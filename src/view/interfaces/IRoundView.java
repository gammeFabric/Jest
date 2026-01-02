package view.interfaces;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;

public interface IRoundView {
    void showRoundStart();
    void showDealCards();
    void showMakeOffers();
    void showDetermineStartingPlayer();
    void showStartingPlayer(Player p, Card faceUpCard);
    void showChoosingPhaseStart();
    void showTurn(Player p);
    void showCardTaken(Player player, Offer takenOffer, Player next);
    void showLastCardTaken(Player player, Offer takenOffer);
    void showDeckEmpty();
    void showRoundEnd();
    void showNoOffers();
}

