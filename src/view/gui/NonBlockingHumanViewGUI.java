package view.gui;

import model.cards.Card;
import model.players.Offer;
import view.interfaces.IHumanView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class NonBlockingHumanViewGUI implements IHumanView {
    private final JFrame mainFrame;
    private final JTextArea outputArea;
    @SuppressWarnings("unused")
    private final JPanel cardPanel; // Kept for potential future use
    private final JPanel handPanel;
    private final InteractionPanel interactionPanel;
    
    // Async result holders
    private final AtomicReference<CompletableFuture<Integer>> cardChoiceFuture = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<Offer>> offerChoiceFuture = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<Boolean>> faceUpDownFuture = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<int[]>> twoCardsFuture = new AtomicReference<>();
    
    // Selection tracking for Full Hand variant
    private ArrayList<Integer> selectedCardIndices;

    public NonBlockingHumanViewGUI(JFrame mainFrame, JTextArea outputArea, JPanel cardPanel, 
                                   JPanel handPanel, JPanel offersPanel, InteractionPanel interactionPanel) {
        this.mainFrame = mainFrame;
        this.outputArea = outputArea;
        this.cardPanel = cardPanel;
        this.handPanel = handPanel;
        this.interactionPanel = interactionPanel;
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    @Override
    public int chooseFaceUpCard(String playerName, ArrayList<Card> hand) {
        appendOutput(playerName + " has " + hand.size() + " cards to make an offer");
        appendOutput("These are your cards:");

        // Clear the hand panel before showing the interaction panel to avoid
        // duplicated card components between the main area and the interaction panel.
        if (handPanel != null) {
            SwingUtilities.invokeLater(() -> {
                handPanel.removeAll();
                handPanel.revalidate();
                handPanel.repaint();
            });
        }

        // Create future for async result
        CompletableFuture<Integer> future = new CompletableFuture<>();
        cardChoiceFuture.set(future);

        // Show interaction panel
        interactionPanel.showChooseFaceUpCard(playerName, hand, choice -> {
            future.complete(choice);
        });

        try {
            return future.get(); // This will block, but the GUI remains responsive
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    @Override
    public Offer chooseOffer(String playerName, ArrayList<Offer> selectableOffers) {
        appendOutput("Choose one from these available offers:");
        for (int i = 0; i < selectableOffers.size(); i++) {
            Offer offer = selectableOffers.get(i);
            appendOutput((i + 1) + ") Offer by " + offer.getOwner().getName() + 
                         " - Face up: " + offer.getFaceUpCard() + ", Face down: [hidden]");
        }

        // Create future for async result
        CompletableFuture<Offer> future = new CompletableFuture<>();
        offerChoiceFuture.set(future);

        // Show interaction panel
        interactionPanel.showChooseOffer(playerName, selectableOffers, choice -> {
            future.complete(choice);
        });

        try {
            return future.get(); // This will block, but the GUI remains responsive
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return selectableOffers.get(0);
        }
    }

    @Override
    public boolean chooseFaceUpOrDown() {
        // Get the selected offer from the previous interaction
        // This is a limitation - we need to store the selected offer from chooseOffer
        // For now, we'll use a simple approach
        
        // Create future for async result
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        faceUpDownFuture.set(future);

        // We need to get the selected offer - this requires state management
        // For now, let's create a dummy offer to show the concept
        // In practice, you'd need to pass the actual selected offer
        
        try {
            // This is a placeholder - you'd need to track the selected offer properly
            appendOutput("Please choose face-up or face-down card from the interaction panel");
            
            // For demo purposes, default to true
            return true;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    // New method to handle face-up/down choice with proper offer context
    public boolean chooseFaceUpOrDown(Offer selectedOffer) {
        appendOutput("Choose card from " + selectedOffer.getOwner().getName() + "'s offer");

        // Create future for async result
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        faceUpDownFuture.set(future);

        // Show interaction panel
        interactionPanel.showChooseFaceUpDown(selectedOffer, choice -> {
            future.complete(choice);
        });

        try {
            return future.get(); // This will block, but the GUI remains responsive
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }

    @Override
    public void showMessage(String message) {
        appendOutput(message);
    }

    @Override
    public void hasNoEnoughCards(String name) {
        appendOutput(name + " doesn't have enough cards to make an offer");
        // Show non-blocking notification instead of modal dialog
        SwingUtilities.invokeLater(() -> {
            JOptionPane pane = new JOptionPane(
                name + " doesn't have enough cards to make an offer", 
                JOptionPane.WARNING_MESSAGE
            );
            JDialog dialog = pane.createDialog(mainFrame, "Insufficient Cards");
            dialog.setModal(false); // Make it non-blocking
            dialog.setVisible(true);
            
            // Auto-hide after 3 seconds
            Timer timer = new Timer(3000, e -> dialog.dispose());
            timer.setRepeats(false);
            timer.start();
        });
    }

    @Override
    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        appendOutput("Thank you. You have chosen " + faceUpCard + 
                    " as a faceUp card and " + faceDownCard + " as a faceDown card");
    }
    
    @Override
    public int[] chooseTwoCardsForOffer(String playerName, ArrayList<Card> hand) {
        appendOutput(playerName + " has " + hand.size() + " cards to make an offer");
        appendOutput("Choose two cards from your hand:");

        // Clear the hand panel before showing the interaction panel to avoid
        // duplicated card components between the main area and the interaction panel.
        if (handPanel != null) {
            SwingUtilities.invokeLater(() -> {
                handPanel.removeAll();
                handPanel.revalidate();
                handPanel.repaint();
            });
        }

        selectedCardIndices = null;
        CompletableFuture<int[]> future = new CompletableFuture<>();
        twoCardsFuture.set(future);

        // Show interaction panel for card selection
        SwingUtilities.invokeLater(() -> {
            interactionPanel.showChooseTwoCards(hand, selectedIndices -> {
                future.complete(selectedIndices);
            });
        });

        try {
            return future.get(); // This will block, but the GUI remains responsive
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return new int[]{0, 1}; // Default fallback
        }
    }

    // Method to cancel any ongoing interactions
    public void cancelInteractions() {
        interactionPanel.hideInteraction();
        
        CompletableFuture<Integer> cardFuture = cardChoiceFuture.get();
        if (cardFuture != null && !cardFuture.isDone()) {
            cardFuture.cancel(true);
        }
        
        CompletableFuture<Offer> offerFuture = offerChoiceFuture.get();
        if (offerFuture != null && !offerFuture.isDone()) {
            offerFuture.cancel(true);
        }
        
        CompletableFuture<Boolean> faceFuture = faceUpDownFuture.get();
        if (faceFuture != null && !faceFuture.isDone()) {
            faceFuture.cancel(true);
        }
    }
}
