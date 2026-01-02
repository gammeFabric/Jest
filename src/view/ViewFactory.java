package view;

import model.players.HumanPlayer;
import model.players.Player;
import view.console.HumanView;
import view.console.VirtualView;
import view.gui.HumanViewGUI;
import view.hybrid.HumanViewHybrid;
import view.interfaces.IPlayerView;

import javax.swing.*;

public class ViewFactory {
    public enum ViewMode {
        CONSOLE, GUI, HYBRID
    }

    private final ViewMode mode;
    private final JFrame mainFrame;
    private final JTextArea outputArea;
    private final JPanel cardPanel;
    private final JPanel handPanel;
    private final JPanel offersPanel;

    public ViewFactory(ViewMode mode) {
        this(mode, null, null, null, null, null);
    }

    public ViewFactory(ViewMode mode, JFrame mainFrame, JTextArea outputArea, JPanel cardPanel, JPanel handPanel, JPanel offersPanel) {
        this.mode = mode;
        this.mainFrame = mainFrame;
        this.outputArea = outputArea;
        this.cardPanel = cardPanel;
        this.handPanel = handPanel;
        this.offersPanel = offersPanel;
    }

    public IPlayerView createPlayerView(Player player) {
        if (player instanceof HumanPlayer) {
            switch (mode) {
                case CONSOLE:
                    return new HumanView();
                case GUI:
                    return new HumanViewGUI(mainFrame, outputArea, cardPanel, handPanel, offersPanel);
                case HYBRID:
                    return new HumanViewHybrid(new HumanView(), 
                                              new HumanViewGUI(mainFrame, outputArea, cardPanel, handPanel, offersPanel));
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

