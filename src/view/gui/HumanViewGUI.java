package view.gui;

import model.cards.Card;
import model.players.Offer;
import view.interfaces.IHumanView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class HumanViewGUI implements IHumanView {
    private final JFrame mainFrame;
    private final JTextArea outputArea;
    private final JPanel cardPanel;
    private final JPanel handPanel;
    private final JPanel offersPanel;
    private Integer selectedCardIndex;
    private Offer selectedOffer;
    private Boolean faceUpChoice;

    private volatile JDialog activeDialog;

    public HumanViewGUI(JFrame mainFrame, JTextArea outputArea, JPanel cardPanel, JPanel handPanel, JPanel offersPanel) {
        this.mainFrame = mainFrame;
        this.outputArea = outputArea;
        this.cardPanel = cardPanel;
        this.handPanel = handPanel;
        this.offersPanel = offersPanel;
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public void cancelActiveDialog() {
        JDialog dialog = activeDialog;
        if (dialog == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                dialog.setVisible(false);
                dialog.dispose();
            } finally {
                if (activeDialog == dialog) {
                    activeDialog = null;
                }
            }
        });
    }

    @Override
    public int chooseFaceUpCard(String playerName, ArrayList<Card> hand) {
        appendOutput(playerName + " has " + hand.size() + " cards to make an offer");
        appendOutput("These are your cards:");

        // Display cards in hand panel
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll();
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                CardButton cardButton = new CardButton(card, true, i, index -> {
                    selectedCardIndex = index;
                });
                handPanel.add(cardButton);
            }
            handPanel.revalidate();
            handPanel.repaint();
        });

        selectedCardIndex = null;
        final CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(() -> {
            // Show dialog with visual cards
            JDialog dialog = new JDialog(mainFrame, "Choose Face-Up Card", true);
            activeDialog = dialog;
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (activeDialog == dialog) {
                        activeDialog = null;
                    }
                    latch.countDown();
                }
            });
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(new Color(0, 100, 0));
            
            JLabel label = new JLabel("Please choose the card to show face-up:", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            dialog.add(label, BorderLayout.NORTH);
            
            JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            cardsPanel.setBackground(new Color(0, 100, 0));
            cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                int finalIndex = i;
                CardButton cardBtn = new CardButton(card, true, i, index -> {
                    selectedCardIndex = finalIndex;
                    dialog.dispose();
                    latch.countDown();
                });
                cardsPanel.add(cardBtn);
            }
            
            dialog.add(cardsPanel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(mainFrame);
            dialog.setVisible(true);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }

        return selectedCardIndex != null ? selectedCardIndex : 0;
    }

    @Override
    public Offer chooseOffer(String playerName, ArrayList<Offer> selectableOffers) {
        appendOutput("Choose one from these available offers:");
        for (int i = 0; i < selectableOffers.size(); i++) {
            Offer offer = selectableOffers.get(i);
            appendOutput((i + 1) + ") Offer by " + offer.getOwner().getName() + 
                         " - Face up: " + offer.getFaceUpCard() + ", Face down: [hidden]");
        }

        selectedOffer = null;
        final CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            // Show dialog with visual offers
            JDialog dialog = new JDialog(mainFrame, "Choose an Offer", true);
            activeDialog = dialog;
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (activeDialog == dialog) {
                        activeDialog = null;
                    }
                    latch.countDown();
                }
            });
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(new Color(0, 100, 0));
            
            JLabel label = new JLabel("Select an offer to take a card from:", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            dialog.add(label, BorderLayout.NORTH);
            
            JPanel offersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            offersPanel.setBackground(new Color(0, 100, 0));
            offersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            for (Offer offer : selectableOffers) {
                OfferComponent offerComp = new OfferComponent(offer, true, selectedOffer -> {
                    HumanViewGUI.this.selectedOffer = selectedOffer;

                    // Highlight the selected offer in the main window offers panel (if present)
                    SwingUtilities.invokeLater(() -> {
                        for (Component comp : HumanViewGUI.this.offersPanel.getComponents()) {
                            if (comp instanceof OfferComponent oc) {
                                oc.setSelected(oc.getOffer() == selectedOffer);
                            } else if (comp instanceof Container container) {
                                for (Component child : container.getComponents()) {
                                    if (child instanceof OfferComponent oc) {
                                        oc.setSelected(oc.getOffer() == selectedOffer);
                                    }
                                }
                            }
                        }
                        HumanViewGUI.this.offersPanel.revalidate();
                        HumanViewGUI.this.offersPanel.repaint();
                    });

                    dialog.dispose();
                    latch.countDown();
                });
                offersPanel.add(offerComp);
            }
            
            dialog.add(offersPanel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(mainFrame);
            dialog.setVisible(true);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return selectableOffers.get(0);
        }

        return selectedOffer != null ? selectedOffer : selectableOffers.get(0);
    }

    @Override
    public boolean chooseFaceUpOrDown() {
        if (selectedOffer == null) {
            return true;
        }

        final CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            // Show dialog with visual card selection
            JDialog dialog = new JDialog(mainFrame, "Choose Card", true);
            activeDialog = dialog;
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (activeDialog == dialog) {
                        activeDialog = null;
                    }
                    latch.countDown();
                }
            });
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(new Color(0, 100, 0));
            
            JLabel label = new JLabel("Take face-up or face-down card?", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            dialog.add(label, BorderLayout.NORTH);
            
            JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
            cardsPanel.setBackground(new Color(0, 100, 0));
            cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Face-up card button
            if (selectedOffer.getFaceUpCard() != null) {
                CardButton faceUpBtn = new CardButton(selectedOffer.getFaceUpCard(), true, 0, index -> {
                    faceUpChoice = true;
                    dialog.dispose();
                    latch.countDown();
                });
                JPanel faceUpPanel = new JPanel(new BorderLayout());
                faceUpPanel.setOpaque(false);
                JLabel faceUpLabel = new JLabel("Face-Up", SwingConstants.CENTER);
                faceUpLabel.setForeground(Color.WHITE);
                faceUpPanel.add(faceUpLabel, BorderLayout.NORTH);
                faceUpPanel.add(faceUpBtn, BorderLayout.CENTER);
                cardsPanel.add(faceUpPanel);
            }
            
            // Face-down card button
            if (selectedOffer.getFaceDownCard() != null) {
                CardButton faceDownBtn = new CardButton(selectedOffer.getFaceDownCard(), false, 1, index -> {
                    faceUpChoice = false;
                    dialog.dispose();
                    latch.countDown();
                });
                JPanel faceDownPanel = new JPanel(new BorderLayout());
                faceDownPanel.setOpaque(false);
                JLabel faceDownLabel = new JLabel("Face-Down", SwingConstants.CENTER);
                faceDownLabel.setForeground(Color.WHITE);
                faceDownPanel.add(faceDownLabel, BorderLayout.NORTH);
                faceDownPanel.add(faceDownBtn, BorderLayout.CENTER);
                cardsPanel.add(faceDownPanel);
            }
            
            dialog.add(cardsPanel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(mainFrame);
            dialog.setVisible(true);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }

        return faceUpChoice != null ? faceUpChoice : true;
    }

    @Override
    public void showMessage(String message) {
        appendOutput(message);
    }

    @Override
    public void hasNoEnoughCards(String name) {
        appendOutput(name + " doesn't have enough cards to make an offer");
        JOptionPane.showMessageDialog(mainFrame, name + " doesn't have enough cards to make an offer", 
                                     "Insufficient Cards", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        appendOutput("Thank you. You have chosen " + faceUpCard + 
                    " as a faceUp card and " + faceDownCard + " as a faceDown card");
    }
}

