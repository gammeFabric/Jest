package view.gui;

import model.cards.Card;
import model.players.Offer;
import model.players.Player;
import view.interfaces.IRoundView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoundViewGUI implements IRoundView {
    private final JTextArea outputArea;

    private final JPanel offersPanel;
    private final JPanel handPanel;
    @SuppressWarnings("unused") 
    private final JPanel botAreasPanel;
    @SuppressWarnings("unused") 
    private final JPanel deckPanel;
    @SuppressWarnings("unused") 
    private final JPanel trophiesPanel;
    private final GameDisplayManager displayManager;
    private final boolean isHybridMode;
    private boolean isInteractionPanelActive = false;

    private final ArrayList<OfferComponent> currentOfferComponents = new ArrayList<>();

    public RoundViewGUI(JTextArea outputArea) {
        this(outputArea, null, null, null, null, null, false);
    }

    public RoundViewGUI(JTextArea outputArea, JPanel offersPanel, JPanel handPanel) {
        this(outputArea, offersPanel, handPanel, null, null, null, false);
    }

    public RoundViewGUI(JTextArea outputArea, JPanel offersPanel, JPanel handPanel,
                       JPanel botAreasPanel, JPanel deckPanel, JPanel trophiesPanel) {
        this(outputArea, offersPanel, handPanel, botAreasPanel, deckPanel, trophiesPanel, false);
    }
    
    public RoundViewGUI(JTextArea outputArea, JPanel offersPanel, JPanel handPanel,
                       JPanel botAreasPanel, JPanel deckPanel, JPanel trophiesPanel, boolean isHybridMode) {
        this.outputArea = outputArea;
        this.offersPanel = offersPanel;
        this.handPanel = handPanel;
        this.botAreasPanel = botAreasPanel;
        this.deckPanel = deckPanel;
        this.trophiesPanel = trophiesPanel;
        this.displayManager = new GameDisplayManager(offersPanel, handPanel, botAreasPanel, deckPanel, trophiesPanel, isHybridMode);
        this.isHybridMode = isHybridMode;
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    @Override
    public void showRoundStart() {
        appendOutput("Round Started");
    }

    @Override
    public void showDealCards() {
        appendOutput("Deal cards to players");
    }

    @Override
    public void showMakeOffers() {
        appendOutput("Players make offers");
    }

    @Override
    public void showDetermineStartingPlayer() {
        appendOutput("Determine starting player");
    }

    @Override
    public void showStartingPlayer(Player p, Card faceUpCard) {
        appendOutput("First to play: " + p.getName() + " (face-up card: " + faceUpCard + ")");
    }

    @Override
    public void showChoosingPhaseStart() {
        appendOutput("\n--- CHOOSING CARDS PHASE ---");
    }

    @Override
    public void showTurn(Player p) {
        appendOutput("\nIt's " + p.getName() + "'s turn to choose a card.");
    }

    @Override
    public void showCardTaken(Player player, Offer takenOffer, Player next) {
        appendOutput(player.getName() + " took " + player.getLastCard() +
                " from " + takenOffer.getOwner().getName() +
                " → next player: " + next.getName());
    }

    @Override
    public void showLastCardTaken(Player player, Offer takenOffer) {
        appendOutput(player.getName() + " took " + player.getLastCard() +
                " → from: " + takenOffer.getOwner().getName());
    }

    @Override
    public void showDeckEmpty() {
        appendOutput("Deck is empty, finalizing round...");
    }

    @Override
    public void showRoundEnd() {
        appendOutput("Round has ended");
    }

    @Override
    public void showNoOffers() {
        appendOutput("No valid offers found. Defaulting to first player.");
    }

    public void showChoosingContext(Player choosingPlayer, ArrayList<Offer> availableOffers) {
        if (displayManager == null || offersPanel == null || handPanel == null || choosingPlayer == null) {
            return;
        }

        isInteractionPanelActive = true; 

        Runnable render = () -> {
            
            
            if (handPanel != null) {
                handPanel.removeAll();
                
                if (!isInteractionPanelActive) {
                    if (choosingPlayer.getHand() != null) {
                        for (model.cards.Card card : choosingPlayer.getHand()) {
                            handPanel.add(new CardComponent(card, true, false));
                        }
                    }
                }
                handPanel.revalidate();
                handPanel.repaint();
            }

            offersPanel.removeAll();
            currentOfferComponents.clear();

            JPanel container = new JPanel(new BorderLayout(10, 10));
            container.setOpaque(false);

            
            JPanel jestPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            jestPanel.setOpaque(false);
            javax.swing.border.TitledBorder jestBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    choosingPlayer.getName() + "'s Jest",
                    0, 0,
                    new Font("Arial", Font.BOLD, 14),
                    Color.WHITE
            );
            jestPanel.setBorder(jestBorder);

            if (choosingPlayer.getJest() != null && choosingPlayer.getJest().getCards() != null) {
                for (model.cards.Card c : choosingPlayer.getJest().getCards()) {
                    jestPanel.add(new CardComponent(c, true, false));
                }
            }

            
            JPanel offersList = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            offersList.setOpaque(false);
            javax.swing.border.TitledBorder offersBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    "Available Offers",
                    0, 0,
                    new Font("Arial", Font.BOLD, 14),
                    Color.WHITE
            );
            offersList.setBorder(offersBorder);

            if (availableOffers != null) {
                for (Offer offer : availableOffers) {
                    if (offer != null && offer.isComplete()) {
                        OfferComponent oc = new OfferComponent(offer, false, null);
                        currentOfferComponents.add(oc);
                        offersList.add(oc);
                    }
                }
            }

            container.add(jestPanel, BorderLayout.NORTH);
            container.add(offersList, BorderLayout.CENTER);
            offersPanel.add(container);

            offersPanel.revalidate();
            offersPanel.repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            render.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(render);
            } catch (Exception ignored) {
                SwingUtilities.invokeLater(render);
            }
        }
    }

    public void resetInteractionFlag() {
        isInteractionPanelActive = false;
    }

    public void highlightChosenOffer(Offer chosenOffer) {
        if (displayManager == null || chosenOffer == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            for (OfferComponent oc : currentOfferComponents) {
                oc.setSelected(oc.getOffer() == chosenOffer);
            }
        });
    }

    public void flashChosenOffer(Offer chosenOffer) {
        if (displayManager == null || chosenOffer == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            for (OfferComponent oc : currentOfferComponents) {
                oc.setSelected(oc.getOffer() == chosenOffer);
            }

            Timer timer = new Timer(650, e -> {
                for (OfferComponent oc : currentOfferComponents) {
                    oc.setSelected(false);
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    public void displayBots(List<Player> bots, List<Offer> offers) {
        if (displayManager != null) {
            displayManager.displayBots(bots, offers);
        }
    }

    public void displayTrophies(List<Card> trophies) {
        if (displayManager != null) {
            displayManager.displayTrophies(trophies);
        }
    }

    public void displayDeck(int deckSize) {
        if (displayManager != null) {
            displayManager.displayDeck(deckSize);
        }
    }
}

