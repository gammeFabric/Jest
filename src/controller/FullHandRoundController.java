package controller;

import app.SaveManager;
import view.interfaces.IGameView;
import view.interfaces.IRoundView;
import view.interfaces.IPlayerView;
import view.ViewFactory;
import model.players.Offer;
import model.players.Player;
import model.game.FullHandRound;
import model.game.Game;
import model.game.Round;
import view.gui.RoundViewGUI;
import view.hybrid.RoundViewHybrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom RoundController for the Full Hand variant.
 * This controller manages the special game flow where all cards are distributed
 * at the beginning and the game continues until each player has one card left.
 */
public class FullHandRoundController {
    private final FullHandRound model;
    private final IRoundView view;
    private final IGameView gameView;
    private final Game gameModel;
    private final Map<Player, PlayerController> playerControllers;
    private final ViewFactory viewFactory;

    public FullHandRoundController(FullHandRound model, IRoundView view, ViewFactory viewFactory, Game gameModel, IGameView gameView) {
        this.model = model;
        this.view = view;
        this.viewFactory = viewFactory;
        this.gameModel = gameModel;
        this.gameView = gameView;
        this.playerControllers = new HashMap<>();
        initializePlayerControllers();
    }

    public void initializePlayerControllers() {
        for (Player player: model.getPlayers()){
            IPlayerView playerView = viewFactory.createPlayerView(player);
            PlayerController controller = PlayerController.createController(player, playerView);
            this.playerControllers.put(player, controller);
        }
    }

    private PlayerController getController(Player player) {
        return playerControllers.get(player);
    }

    /**
     * Plays a round according to the Full Hand variant rules.
     * Instead of dealing cards each turn, all cards are distributed at the beginning
     * and the game continues until each player has only one card left.
     */
    public void playRound() {
        view.showRoundStart();

        // Distribute all cards at the beginning instead of dealing each turn
        view.showDealCards();
        model.distributeAllCards();

        // Show initial card distribution
        showInitialDistribution(model.getInitialCardsPerPlayer());

        // Continue playing until each player has only one card left
        while (!model.isOver()) {
            playFullHandTurn();

            // After each turn, allow user to save the game (same behavior as standard variant)
            try {
                if (gameView != null && gameView.askSaveAfterRound()) {
                    String saveName = gameView.askSaveName();
                    SaveManager.save(gameModel, saveName);
                }
            } catch (Exception e) {
                // Don't let save issues interrupt gameplay; log for debugging
                System.err.println("Warning: failed to save game after full-hand turn: " + e.getMessage());
            }
        }

        // Finalize the round by adding last cards to Jests
        model.finalizeRound();
        view.showRoundEnd();
    }

    /**
     * Plays a single turn in the Full Hand variant.
     * Each turn, players make offers from their remaining cards.
     */
    private void playFullHandTurn() {
        view.showMakeOffers();
        makeOffersPhase();

        view.showDetermineStartingPlayer();
        Player startingPlayer = model.determineStartingPlayer();
        
        if (startingPlayer != null && startingPlayer.getOffer() != null) {
            view.showStartingPlayer(startingPlayer, startingPlayer.getOffer().getFaceUpCard());
            playChoosingPhase(startingPlayer);
        }

        // Return remaining offer cards to players' hands
        returnRemainingOfferCards();

        // Check if the game should end after this turn
        if (model.isOver()) {
            showGameEnding();
        }
    }

    /**
     * Modified choosing phase for Full Hand variant.
     * Continues until all players have had a chance to choose an offer.
     */
    public void playChoosingPhase(Player startingPlayer) {
        view.showChoosingPhaseStart();
        Player currentPlayer = startingPlayer;
        int turns = 0;
        int maxTurns = model.getPlayers().size();
        model.setAlreadyPlayed(new ArrayList<>());
        
        while (turns < maxTurns) {
            view.showTurn(currentPlayer);

            ArrayList<Offer> availableOffers = model.getAvailableOffers();
            if (availableOffers.isEmpty()) {
                break; // No offers available
            }

            if (view instanceof RoundViewGUI gui) {
                gui.showChoosingContext(currentPlayer, availableOffers);
            } else if (view instanceof RoundViewHybrid hybrid) {
                hybrid.showChoosingContext(currentPlayer, availableOffers);
            }

            PlayerController controller = getController(currentPlayer);
            Offer takenOffer = controller.chooseCard(availableOffers);

            if (takenOffer != null) {
                handleTakenOffer(currentPlayer, takenOffer);
            }

            model.getAlreadyPlayed().add(currentPlayer);
            
            if (model.getAlreadyPlayed().size() <= maxTurns - 1) {
                Player nextPlayer = model.getNextPlayer(model.getAlreadyPlayed(), takenOffer);
                view.showCardTaken(currentPlayer, takenOffer, nextPlayer);
                currentPlayer = nextPlayer;
            } else {
                view.showLastCardTaken(currentPlayer, takenOffer);
            }
            turns++;
        }
    }

    /**
     * Handles the offer-making phase.
     */
    private void makeOffersPhase() {
        for (Player player: model.getPlayers()){
            PlayerController controller = getController(player);
            Offer offer = controller.makeOffer();
            if (offer != null) {
                model.addOffer(offer);
            } else {
                System.err.println("Warning: Player " + player.getName() + " returned null offer");
            }
        }
    }

    /**
     * Handles the visual feedback when an offer is taken.
     */
    private void handleTakenOffer(Player currentPlayer, Offer takenOffer) {
        boolean isBotTurn = currentPlayer.isVirtual();
        if (view instanceof RoundViewGUI gui) {
            if (isBotTurn) {
                gui.flashChosenOffer(takenOffer);
            } else {
                gui.highlightChosenOffer(takenOffer);
            }
            // Reset interaction flag after the choice is complete
            gui.resetInteractionFlag();
        } else if (view instanceof RoundViewHybrid hybrid) {
            if (isBotTurn) {
                hybrid.flashChosenOffer(takenOffer);
            } else {
                hybrid.highlightChosenOffer(takenOffer);
            }
        }
    }

    /**
     * Shows initial distribution information.
     */
    private void showInitialDistribution(int cardsPerPlayer) {
        // For now, we'll just show a message about the distribution
        // The actual hand display would be handled by the existing view methods
        // during the offer phase
    }

    /**
     * Returns remaining offer cards to players' hands after a turn.
     * This ensures that cards not taken by other players return to their owner.
     */
    private void returnRemainingOfferCards() {
        for (Player player : model.getPlayers()) {
            Offer offer = player.getOffer();
            if (offer != null) {
                // Return face-up card if it hasn't been taken
                if (offer.getFaceUpCard() != null) {
                    player.addToHand(offer.getFaceUpCard());
                }
                // Return face-down card if it hasn't been taken
                if (offer.getFaceDownCard() != null) {
                    player.addToHand(offer.getFaceDownCard());
                }
                // Clear the offer for the next turn
                player.setOffer(null);
            }
        }
    }

    /**
     * Shows game ending message.
     */
    private void showGameEnding() {
        // This would need to be added to the IRoundView interface
        // For now, we'll use existing methods
        view.showRoundEnd();
    }

    /**
     * Gets the FullHandRound model.
     * @return the FullHandRound instance
     */
    public FullHandRound getModel() {
        return model;
    }

    /**
     * Gets the round counter.
     */
    public int getRoundCounter() {
        return Round.getRoundCounter();
    }
}
