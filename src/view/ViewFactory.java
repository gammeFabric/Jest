package view;

import model.players.HumanPlayer;
import model.players.Player;
import view.console.HumanView;
import view.console.VirtualView;
import view.gui.HumanViewGUI;
import view.gui.InteractionPanel;
import view.hybrid.HumanViewHybrid;
import view.interfaces.IPlayerView;

import javax.swing.*;

public class ViewFactory {
    public enum ViewMode {
        CONSOLE, GUI, HYBRID
    }

    private final ViewMode mode;
    private final JTextArea outputArea;
    private final JPanel cardPanel;
    private final JPanel handPanel;
    private final JPanel offersPanel;
    private final InteractionPanel interactionPanel;

    public ViewFactory(ViewMode mode) {
        this(mode, null, null, null, null, null);
    }

    public ViewFactory(ViewMode mode, JTextArea outputArea, JPanel cardPanel, JPanel handPanel, JPanel offersPanel, InteractionPanel interactionPanel) {
        this.mode = mode;
        this.outputArea = outputArea;
        this.cardPanel = cardPanel;
        this.handPanel = handPanel;
        this.offersPanel = offersPanel;
        this.interactionPanel = interactionPanel;
    }

    public IPlayerView createPlayerView(Player player) {
        if (player instanceof HumanPlayer) {
            switch (mode) {
                case CONSOLE:
                    return new HumanView();
                case GUI:
                    return new HumanViewGUI(outputArea, cardPanel, handPanel, offersPanel, interactionPanel);
                case HYBRID:
                    return new HumanViewHybrid(new HumanView(), 
                                              new HumanViewGUI(outputArea, cardPanel, handPanel, offersPanel, interactionPanel, true));
                default:
                    return new HumanView();
            }
        } else {
            switch (mode) {
                case CONSOLE:
                    return new VirtualView();
                case GUI:
                    return new VirtualView();
                case HYBRID:
                    return new VirtualView();
                default:
                    return new VirtualView();
            }
        }
    }
}

