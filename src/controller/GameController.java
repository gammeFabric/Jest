package controller;

import model.cards.CardEffect;
import model.cards.ExtensionCard; // Import nécessaire
import model.players.Player;
import model.players.ScoreVisitorImpl;
import model.players.strategies.StrategyType;
import model.game.Round;
import view.console.GameView;
import model.game.Game;
import view.console.RoundView;

import java.util.ArrayList;
import java.util.List; // Import nécessaire

public class GameController {
    private final Game model;
    private final GameView consoleView;

    public GameController(Game model, GameView consoleView) {
        this.model = model;
        this.consoleView = consoleView;
    }
//    private final GameView guiView;

    public void addPlayers(){
        int playerCount = consoleView.askNumberOfPlayers();
        for (int i = 1; i <= playerCount; i++) {
            String name = consoleView.askPlayerName(i);
            boolean isHuman = consoleView.isHumanPlayer(name);
            if  (isHuman) {
                model.addHumanPlayer(name);
            }
            else {
                StrategyType strategy = consoleView.askStrategy(name);
                model.addVirtualPlayer(name, strategy);
            }
        }
        consoleView.showPlayers(model.getPlayers());
    }

    public void startGame(){
        addPlayers();
        
        // Vérification du nombre de joueurs
        if (model.getPlayers().size() < 3 || model.getPlayers().size() > 4) {
            throw new IllegalStateException("Jest supports 3 or 4 players only.");
        }

        // --- NOUVEAU : GESTION DES EXTENSIONS ---
        handleExtensions(); 
        // ----------------------------------------

        model.chooseTrophies(model.getPlayers().size());
        consoleView.showTrophies(model.trophiesInfo());

        playGame();
    }

    /**
     * Gère la logique de proposition et d'ajout des extensions.
     */
    private void handleExtensions() {
        ArrayList<ExtensionCard> availableExtensions = new ArrayList<>();

        // --- 1. THE SHIELD ---
        // Stratégie Joueur : Active des flags de protection.
        // Stratégie Bot : Le bot PRUDENT adore cette carte (valeur 1000), l'AGRESSIF s'en fiche un peu.
        availableExtensions.add(new ExtensionCard(
            "The Shield", 0, 
            "Protège contre les malus : vos cartes Carreau et Coeur ne valent plus de points négatifs.",
            new CardEffect() {
                @Override
                public void applyOnVisit(ScoreVisitorImpl visitor) {
                    visitor.setFlag("NO_NEGATIVE_DIAMONDS", true);
                    visitor.setFlag("NO_NEGATIVE_HEARTS", true);
                }
            },
            (strategy, jest) -> {
                if (strategy == StrategyType.CAUTIOUS) return 1000; // Priorité absolue
                return 0; // Valeur faible pour les autres
            }
        ));

        // --- 2. THE CROWN ---
        // Stratégie Joueur : Bonus fixe.
        // Stratégie Bot : Tout le monde aime les points gratuits (Valeur 20 > Valeur max d'une carte normale).
        availableExtensions.add(new ExtensionCard(
            "The Crown", 0, 
            "Un trésor royal : Ajoute 5 points directement à votre score final.",
            new CardEffect() {
                @Override
                public int calculateBonus(ScoreVisitorImpl visitor) {
                    return 5;
                }
            },
            (strategy, jest) -> 20 
        ));

        // --- 3. THE SPY ---
        // Stratégie Joueur : Valeur faciale 2.
        // Stratégie Bot : Vaut simplement sa valeur faciale (2).
        availableExtensions.add(new ExtensionCard(
            "The Spy", 2, 
            "Un agent infiltré : Carte neutre de valeur 2.",
            new CardEffect() {}, // Effet vide
            (strategy, jest) -> 2
        ));

        // --- 4. THE JESTER ---
        // Stratégie Joueur : Bonus conditionnel.
        // Stratégie Bot : 
        // - Le PRUDENT le fuit (-100).
        // - L'AGRESSIF le prend SI il a déjà le Joker, sinon il l'évite un peu.
        availableExtensions.add(new ExtensionCard(
            "The Jester", 0,
            "L'ami du Joker : Vaut +10 si vous avez le Joker, sinon -5.",
            new CardEffect() {
                @Override
                public int calculateBonus(ScoreVisitorImpl visitor) {
                    return visitor.hasJoker() ? 10 : -5;
                }
            },
            (strategy, jest) -> {
                // On vérifie si le bot possède déjà le Joker dans son Jest
                boolean hasJoker = jest.getCards().stream()
                        .anyMatch(c -> c instanceof model.cards.Joker);

                if (strategy == StrategyType.CAUTIOUS) return -100; // Trop risqué
                
                // L'agressif tente le coup s'il a le Joker
                if (hasJoker) return 50; 
                return -5; // Sinon c'est un malus
            }
        ));

        boolean validSelection = false;
        
        while (!validSelection) {
            // 1. Demander à la vue
            List<Integer> selectedIndices = consoleView.askForExtensions(availableExtensions);

            // 2. Récupérer les données du jeu
            int playerCount = model.getPlayers().size();
            int baseDeckSize = 17; // Taille standard du paquet Jest
            
            // 3 joueurs -> 2 trophées retirés
            // 4 joueurs -> 1 trophée retiré
            int trophiesCount = (playerCount == 3) ? 2 : 1; 

            // 3. Calculs mathématiques
            int extensionsCount = selectedIndices.size();
            int playableDeckSize = baseDeckSize + extensionsCount - trophiesCount;

            // Combien de tours complets peut-on faire ?
            float totalRounds = (float)playableDeckSize / playerCount;

            // 4. Vérification de la viabilité
            // Le nombre de tours doit être un entier (pas de demi-tour possible)
            if (totalRounds != Math.floor(totalRounds)) {
                consoleView.showInvalidExtensionMessage("The selected extensions lead to an invalid deck size. Please choose again.");
                continue; // Recommencer la sélection
            }
            validSelection = true;
            
            if (!selectedIndices.isEmpty()) {
                ArrayList<ExtensionCard> cardsToAdd = new ArrayList<>();
                System.out.println("Validation OK. Ajout des cartes au paquet :");
                        
                for (int index : selectedIndices) {
                ExtensionCard card = availableExtensions.get(index);
                cardsToAdd.add(card);
                System.out.println(" [+] " + card.getName());
                }
                
                model.getDeck().addExtensions(cardsToAdd);     
            } else {
                System.out.println("Aucune extension sélectionnée. Partie standard.");
            }
        }
        
    }

    public void playGame() {
        while (!model.getDeck().isEmpty()) {
            RoundController roundController = new RoundController(new Round(model.getPlayers(), model.getDeck()), new RoundView());

            consoleView.showRound(roundController.getRoundCounter());
            roundController.playRound();
            // somehow add rounds to rounds in game
//            rounds.add(currentRound);

            //change to take a deck from model not from round (reference on the same object)
            if (model.getDeck().isEmpty())
                break;
        }
        endGame();
    }

    public void endGame() {
        for (Player player : model.getPlayers()) {
            player.takeRemainingOfferCard();
        }

        consoleView.showEndRoundMessage();
        model.assignTrophies();
        model.calculateAllScores();

        for (Player player : model.getPlayers()) {
            consoleView.showScore(player);
        }

//        Player winner = getWinner();
//        view.showWinner(winner);
        ArrayList<Player> winners = model.getWinners();
        consoleView.showWinners(winners);

    }
}