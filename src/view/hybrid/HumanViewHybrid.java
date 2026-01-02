package view.hybrid;

import model.cards.Card;
import model.players.Offer;
import view.console.HumanView;
import view.gui.HumanViewGUI;
import view.interfaces.IHumanView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HumanViewHybrid implements IHumanView {
    private final HumanView consoleView;
    private final HumanViewGUI guiView;

    static final BufferedReader CONSOLE_READER = new BufferedReader(new InputStreamReader(System.in));

    private static final class Choice<T> {
        private final Object lock = new Object();
        private boolean resolved;
        private T value;

        boolean isResolved() {
            synchronized (lock) {
                return resolved;
            }
        }

        void resolve(T value) {
            synchronized (lock) {
                if (resolved) {
                    return;
                }
                resolved = true;
                this.value = value;
                lock.notifyAll();
            }
        }

        T await() {
            synchronized (lock) {
                while (!resolved) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return value;
                    }
                }
                return value;
            }
        }
    }

    private static String readLineNonBlocking(Choice<?> choice, String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }

        while (!choice.isResolved()) {
            try {
                if (CONSOLE_READER.ready()) {
                    return CONSOLE_READER.readLine();
                }
            } catch (IOException e) {
                return null;
            }

            synchronized (choice.lock) {
                if (choice.isResolved()) {
                    return null;
                }
                try {
                    choice.lock.wait(75);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    private static void runGuiAsync(String threadName, Runnable action) {
        Thread t = new Thread(action, threadName);
        t.setDaemon(true);
        t.start();
    }

    public HumanViewHybrid(HumanView consoleView, HumanViewGUI guiView) {
        this.consoleView = consoleView;
        this.guiView = guiView;
    }

    @Override
    public int chooseFaceUpCard(String playerName, ArrayList<Card> hand) {
        consoleView.showMessage(playerName + " has " + hand.size() + " cards to make an offer");
        consoleView.showMessage("These are your cards:");
        for (int i = 0; i < hand.size(); i++) {
            consoleView.showMessage((i + 1) + ": " + hand.get(i));
        }

        Choice<Integer> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-ChooseFaceUpCard", () -> {
            int idx = guiView.chooseFaceUpCard(playerName, hand);
            choice.resolve(idx);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Please choose the number of the card to show face-up: ");
                if (line == null) {
                    return;
                }
                try {
                    int faceUpIndex = Integer.parseInt(line.trim()) - 1;
                    if (faceUpIndex >= 0 && faceUpIndex < hand.size()) {
                        choice.resolve(faceUpIndex);
                        guiView.cancelActiveDialog();
                        return;
                    }
                } catch (NumberFormatException ignored) {
                }
                consoleView.showMessage("Please enter a valid number.");
            }
        }, "Hybrid-Console-ChooseFaceUpCard");
        consoleThread.setDaemon(true);
        consoleThread.start();

        Integer resolved = choice.await();
        return resolved != null ? resolved : 0;
    }

    @Override
    public Offer chooseOffer(String playerName, ArrayList<Offer> selectableOffers) {
        consoleView.showMessage("Choose one from these available offers:");
        for (int i = 0; i < selectableOffers.size(); i++) {
            Offer offer = selectableOffers.get(i);
            consoleView.showMessage((i + 1) + ") Offer by " + offer.getOwner().getName() +
                    " - Face up: " + offer.getFaceUpCard() + ", Face down: [hidden]");
        }

        Choice<Offer> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-ChooseOffer", () -> {
            Offer offer = guiView.chooseOffer(playerName, selectableOffers);
            choice.resolve(offer);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Choose the number of the offer: ");
                if (line == null) {
                    return;
                }
                try {
                    int idx = Integer.parseInt(line.trim());
                    if (idx >= 1 && idx <= selectableOffers.size()) {
                        choice.resolve(selectableOffers.get(idx - 1));
                        guiView.cancelActiveDialog();
                        return;
                    }
                } catch (NumberFormatException ignored) {
                }
                consoleView.showMessage("Please enter a valid number.");
            }
        }, "Hybrid-Console-ChooseOffer");
        consoleThread.setDaemon(true);
        consoleThread.start();

        Offer resolved = choice.await();
        return resolved != null ? resolved : selectableOffers.get(0);
    }

    @Override
    public boolean chooseFaceUpOrDown() {
        consoleView.showMessage("Take 1) Face-up card or 2) Face-down card?");

        Choice<Boolean> choice = new Choice<>();

        runGuiAsync("Hybrid-GUI-ChooseFaceUpOrDown", () -> {
            boolean result = guiView.chooseFaceUpOrDown();
            choice.resolve(result);
        });

        Thread consoleThread = new Thread(() -> {
            while (!choice.isResolved()) {
                String line = readLineNonBlocking(choice, "Take 1) Face-up card or 2) Face-down card? ");
                if (line == null) {
                    return;
                }
                String trimmed = line.trim();
                if (trimmed.equals("1")) {
                    choice.resolve(true);
                    guiView.cancelActiveDialog();
                    return;
                }
                if (trimmed.equals("2")) {
                    choice.resolve(false);
                    guiView.cancelActiveDialog();
                    return;
                }
                consoleView.showMessage("Please enter 1 or 2.");
            }
        }, "Hybrid-Console-ChooseFaceUpOrDown");
        consoleThread.setDaemon(true);
        consoleThread.start();

        Boolean resolved = choice.await();
        return resolved != null ? resolved : true;
    }

    @Override
    public void showMessage(String message) {
        consoleView.showMessage(message);
        guiView.showMessage(message);
    }

    @Override
    public void hasNoEnoughCards(String name) {
        consoleView.hasNoEnoughCards(name);
        guiView.hasNoEnoughCards(name);
    }

    @Override
    public void thankForChoosing(Card faceUpCard, Card faceDownCard) {
        consoleView.thankForChoosing(faceUpCard, faceDownCard);
        guiView.thankForChoosing(faceUpCard, faceDownCard);
    }
}

