package app;

import controller.GameController;
import view.console.GameView;
import model.game.Game;

public class GameLauncher {
    public static void main(String[] args) {
        System.out.println("--- Jest Card Game ---");

        Game model = new Game();

        GameView view = new GameView();

        GameController controller = new GameController(model, view);

        controller.startGame();
    }
}
