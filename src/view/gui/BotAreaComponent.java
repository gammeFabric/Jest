package view.gui;

import model.players.Offer;
import model.players.Player;

import javax.swing.*;
import java.awt.*;

public class BotAreaComponent extends JPanel {
    private final Player bot;
    private final Offer offer;

    public BotAreaComponent(Player bot, Offer offer) {
        this.bot = bot;
        this.offer = offer;
        
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(200, 220, 240));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(250, 180));

        // Bot name label at top
        JLabel nameLabel = new JLabel(bot.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        add(nameLabel, BorderLayout.NORTH);

        // Cards panel (2 face-up + 2 face-down)
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        cardsPanel.setOpaque(false);

        if (offer != null) {
            // Face-up card
            if (offer.getFaceUpCard() != null) {
                CardComponent faceUpCard = new CardComponent(offer.getFaceUpCard(), true, false);
                cardsPanel.add(faceUpCard);
            }
            
            // Face-down card
            if (offer.getFaceDownCard() != null) {
                CardComponent faceDownCard = new CardComponent(offer.getFaceDownCard(), false, false);
                cardsPanel.add(faceDownCard);
            }
        } else {
            // Show placeholder cards if no offer yet
            cardsPanel.add(new CardComponent(null, true, false));
            cardsPanel.add(new CardComponent(null, false, false));
        }

        add(cardsPanel, BorderLayout.CENTER);
    }

    public Player getBot() {
        return bot;
    }

    public Offer getOffer() {
        return offer;
    }
}

