package view.gui;

import model.cards.Card;
import model.players.Offer;
import view.interfaces.IHumanView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class HumanViewGUI implements IHumanView {
    private final JTextArea outputArea;
    @SuppressWarnings("unused")
    private final JPanel cardPanel; 
    private final JPanel handPanel;
    private final InteractionPanel interactionPanel;
    private Offer selectedOffer;
    private final boolean isHybridMode;
    private boolean isInteractionPanelActive = false;

    
    private final AtomicReference<CompletableFuture<Integer>> cardChoiceFuture = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<Offer>> offerChoiceFuture = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<Boolean>> faceUpDownFuture = new AtomicReference<>();
    private final AtomicReference<CompletableFuture<int[]>> twoCardsFuture = new AtomicReference<>();
    
    
    private ArrayList<Integer> selectedCardIndices;

    public HumanViewGUI(JTextArea outputArea, JPanel cardPanel, JPanel handPanel, JPanel offersPanel, InteractionPanel interactionPanel) {
        this(outputArea, cardPanel, handPanel, offersPanel, interactionPanel, false);
    }
    
    public HumanViewGUI(JTextArea outputArea, JPanel cardPanel, JPanel handPanel, JPanel offersPanel, InteractionPanel interactionPanel, boolean isHybridMode) {
        this.outputArea = outputArea;
        this.cardPanel = cardPanel;
        this.handPanel = handPanel;
        this.interactionPanel = interactionPanel;
        this.isHybridMode = isHybridMode;
        
        
        if (interactionPanel != null) {
            interactionPanel.setOnInteractionHidden(() -> {
                isInteractionPanelActive = false;
            });
        }
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public void cancelActiveDialog() {
        cancelInteractions();
    }

    
    public void cancelInteractions() {
        isInteractionPanelActive = false; 
        
        if (interactionPanel != null) {
            interactionPanel.hideInteraction();
        }
        
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
    
    private boolean isHybridMode() {
        return isHybridMode;
    }

    @Override
    public int chooseFaceUpCard(String playerName, ArrayList<Card> hand) {
        appendOutput(playerName + " has " + hand.size() + " cards to make an offer");
        appendOutput("These are your cards:");

        
        
        if (handPanel != null) {
            SwingUtilities.invokeLater(() -> {
                handPanel.removeAll();
                handPanel.revalidate();
                handPanel.repaint();
            });
        }
        
        isInteractionPanelActive = true;

        
        CompletableFuture<Integer> future = new CompletableFuture<>();
        cardChoiceFuture.set(future);

        
        interactionPanel.showChooseFaceUpCard(playerName, hand, choice -> {
            isInteractionPanelActive = false; 
            future.complete(choice);
        });

        try {
            return future.get(); 
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

        
        CompletableFuture<Offer> future = new CompletableFuture<>();
        offerChoiceFuture.set(future);

        
        interactionPanel.showChooseOffer(playerName, selectableOffers, choice -> {
            selectedOffer = choice;
            isInteractionPanelActive = false; 
            future.complete(choice);
        });

        try {
            return future.get(); 
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return selectableOffers.get(0);
        }
    }

    @Override
    public boolean chooseFaceUpOrDown() {
        if (selectedOffer == null) {
            return true;
        }

        
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        faceUpDownFuture.set(future);

        
        interactionPanel.showChooseFaceUpDown(selectedOffer, choice -> {
            isInteractionPanelActive = false; 
            future.complete(choice);
        });

        try {
            return future.get(); 
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
        appendOutput(name + " doesn't have enough cards to make an offer!");
    }
    
    @Override
    public int[] chooseTwoCardsForOffer(String playerName, ArrayList<Card> hand) {
        appendOutput(playerName + " has " + hand.size() + " cards to make an offer");
        appendOutput("Choose two cards from your hand:");

        
        
        if (handPanel != null) {
            SwingUtilities.invokeLater(() -> {
                handPanel.removeAll();
                handPanel.revalidate();
                handPanel.repaint();
            });
        }
        
        isInteractionPanelActive = true;

        selectedCardIndices = null;
        CompletableFuture<int[]> future = new CompletableFuture<>();
        twoCardsFuture.set(future);

        
        SwingUtilities.invokeLater(() -> {
            interactionPanel.showChooseTwoCards(hand, selectedIndices -> {
                isInteractionPanelActive = false; 
                future.complete(selectedIndices);
            });
        });

        try {
            return future.get(); 
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return new int[]{0, 1}; 
        }
    }

    @Override
    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        appendOutput("Thank you. You have chosen " + faceUpCard + 
                    " as a faceUp card and " + faceDownCard + " as a faceDown card");
    }
}

