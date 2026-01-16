package view.gui;

import model.cards.Card;
import model.players.Offer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Panneau GUI pour les interactions du joueur humain.
 * 
 * <p>Ce composant gère l'affichage dynamique des dialogues d'interaction
 * et la capture des décisions du joueur humain pendant le jeu.</p>
 * 
 * <p><b>Types d'interactions :</b></p>
 * <ul>
 *   <li>Sélection de la carte face visible</li>
 *   <li>Choix d'une offre adverse</li>
 *   <li>Choix entre face up ou face down</li>
 *   <li>Sélection de deux cartes pour l'offre</li>
 * </ul>
 */
public class InteractionPanel extends JPanel {
    public enum InteractionType {
        CHOOSE_FACE_UP_CARD,
        CHOOSE_OFFER,
        CHOOSE_FACE_UP_DOWN,
        CHOOSE_TWO_CARDS,
        NONE
    }

    private InteractionType currentInteraction = InteractionType.NONE;
    private final GameWindow gameWindow;
    
    
    private Consumer<Integer> onCardSelected;
    private Consumer<Offer> onOfferSelected;
    private Consumer<Boolean> onFaceUpChoice;
    private Consumer<int[]> onTwoCardsSelected;
    private Runnable onInteractionHidden; 
    
    
    private ArrayList<Offer> currentOffers;
    private ArrayList<Card> currentCards;
    
    
    private final JLabel instructionLabel;
    private final JPanel contentPanel;
    private final JButton cancelButton;
    
    public InteractionPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 255, 240));
        setBorder(BorderFactory.createTitledBorder("Player Action"));
        
        
        instructionLabel = new JLabel(" ", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        instructionLabel.setForeground(Color.BLACK);
        add(instructionLabel, BorderLayout.NORTH);
        
        
        contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        contentPanel.setBackground(new Color(255, 255, 240));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.addActionListener(this::onCancel);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(255, 255, 240));
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(false);
    }
    
    public void showChooseFaceUpCard(String playerName, ArrayList<Card> hand, Consumer<Integer> callback) {
        this.currentInteraction = InteractionType.CHOOSE_FACE_UP_CARD;
        this.onCardSelected = callback;
        
        SwingUtilities.invokeLater(() -> {
            instructionLabel.setText(playerName + ": Choose a card to show face-up");
            contentPanel.removeAll();
            
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                int finalIndex = i;
                CardButton cardButton = new CardButton(card, true, i, index -> {
                        
                        callback.accept(finalIndex);
                    hideInteraction();
                });
                contentPanel.add(cardButton);
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
            setVisible(true);
            gameWindow.getGameAreaPanel().revalidate();
            gameWindow.getGameAreaPanel().repaint();
        });
    }
    
    public void showChooseOffer(String playerName, ArrayList<Offer> offers, Consumer<Offer> callback) {
        this.currentInteraction = InteractionType.CHOOSE_OFFER;
        this.currentOffers = offers;
        this.onOfferSelected = callback;
        
        SwingUtilities.invokeLater(() -> {
            instructionLabel.setText(playerName + ": Select an offer to take a card from");
            contentPanel.removeAll();
            
            for (Offer offer : offers) {
                OfferComponent offerComp = new OfferComponent(offer, true, selectedOffer -> {
                    
                    callback.accept(selectedOffer);
                    hideInteraction();
                });
                contentPanel.add(offerComp);
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
            setVisible(true);
            gameWindow.getGameAreaPanel().revalidate();
            gameWindow.getGameAreaPanel().repaint();
        });
    }
    
    public void showChooseFaceUpDown(Offer offer, Consumer<Boolean> callback) {
        this.currentInteraction = InteractionType.CHOOSE_FACE_UP_DOWN;
        this.onFaceUpChoice = callback;
        
        SwingUtilities.invokeLater(() -> {
            instructionLabel.setText("Take face-up or face-down card?");
            contentPanel.removeAll();
            
            
            if (offer.getFaceUpCard() != null) {
                JPanel faceUpPanel = new JPanel(new BorderLayout());
                faceUpPanel.setOpaque(false);
                
                JLabel faceUpLabel = new JLabel("Face-Up", SwingConstants.CENTER);
                faceUpLabel.setFont(new Font("Arial", Font.BOLD, 12));
                faceUpPanel.add(faceUpLabel, BorderLayout.NORTH);
                
                CardButton faceUpBtn = new CardButton(offer.getFaceUpCard(), true, 0, index -> {
                    
                    callback.accept(true);
                    hideInteraction();
                });
                faceUpPanel.add(faceUpBtn, BorderLayout.CENTER);
                contentPanel.add(faceUpPanel);
            }
            
            
            if (offer.getFaceDownCard() != null) {
                JPanel faceDownPanel = new JPanel(new BorderLayout());
                faceDownPanel.setOpaque(false);
                
                JLabel faceDownLabel = new JLabel("Face-Down", SwingConstants.CENTER);
                faceDownLabel.setFont(new Font("Arial", Font.BOLD, 12));
                faceDownPanel.add(faceDownLabel, BorderLayout.NORTH);
                
                CardButton faceDownBtn = new CardButton(offer.getFaceDownCard(), false, 1, index -> {
                    
                    callback.accept(false);
                    hideInteraction();
                });
                faceDownPanel.add(faceDownBtn, BorderLayout.CENTER);
                contentPanel.add(faceDownPanel);
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
            setVisible(true);
            gameWindow.getGameAreaPanel().revalidate();
            gameWindow.getGameAreaPanel().repaint();
        });
    }
    
    public void showChooseTwoCards(ArrayList<Card> hand, Consumer<int[]> callback) {
        this.currentInteraction = InteractionType.CHOOSE_TWO_CARDS;
        this.currentCards = hand;
        this.onTwoCardsSelected = callback;
        
        SwingUtilities.invokeLater(() -> {
            instructionLabel.setText("Choose two cards for your offer:");
            contentPanel.removeAll();
            
            ArrayList<Integer> selectedIndices = new ArrayList<>();
            
            CardButton[] cardButtonRef = new CardButton[hand.size()]; 
            for (int i = 0; i < hand.size(); i++) {
                final int cardIndex = i; 
                Card card = hand.get(cardIndex);
                cardButtonRef[cardIndex] = new CardButton(card, true, cardIndex, index -> {
                    if (selectedIndices.contains(cardIndex)) {
                        selectedIndices.remove(Integer.valueOf(cardIndex));
                        cardButtonRef[cardIndex].setSelected(false);
                    } else if (selectedIndices.size() < 2) {
                        selectedIndices.add(cardIndex);
                        cardButtonRef[cardIndex].setSelected(true);
                        
                        if (selectedIndices.size() == 2) {
                            
                            int[] result = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
                            callback.accept(result);
                            hideInteraction();
                        }
                    }
                });
                contentPanel.add(cardButtonRef[cardIndex]);
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
            setVisible(true);
            gameWindow.getGameAreaPanel().revalidate();
            gameWindow.getGameAreaPanel().repaint();
        });
    }
    
    public void setOnInteractionHidden(Runnable callback) {
        this.onInteractionHidden = callback;
    }
    
    public void hideInteraction() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            currentInteraction = InteractionType.NONE;
            contentPanel.removeAll();
            instructionLabel.setText(" ");
            currentOffers = null;
            currentCards = null;
            onCardSelected = null;
            onOfferSelected = null;
            onFaceUpChoice = null;
            onTwoCardsSelected = null;
            
            
            if (onInteractionHidden != null) {
                onInteractionHidden.run();
            }
            
            gameWindow.getGameAreaPanel().revalidate();
            gameWindow.getGameAreaPanel().repaint();
        });
    }
    
    private void onCancel(ActionEvent e) {
        switch (currentInteraction) {
            case CHOOSE_FACE_UP_CARD:
                if (onCardSelected != null) {
                    onCardSelected.accept(0); 
                }
                break;
            case CHOOSE_OFFER:
                if (onOfferSelected != null && currentOffers != null && !currentOffers.isEmpty()) {
                    onOfferSelected.accept(currentOffers.get(0)); 
                }
                break;
            case CHOOSE_FACE_UP_DOWN:
                if (onFaceUpChoice != null) {
                    onFaceUpChoice.accept(true); 
                }
                break;
            case CHOOSE_TWO_CARDS:
                if (onTwoCardsSelected != null && currentCards != null) {
                    
                    onTwoCardsSelected.accept(new int[]{0, 1});
                }
                break;
            default:
                break;
        }
        hideInteraction();
    }
    
    public boolean hasActiveInteraction() {
        return currentInteraction != InteractionType.NONE;
    }
    
    public InteractionType getCurrentInteraction() {
        return currentInteraction;
    }
}
