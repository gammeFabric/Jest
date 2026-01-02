package model.cards;

import model.players.Jest;
import model.players.strategies.StrategyType;

 import model.game.ExtensionManager;

 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
import java.util.function.BiFunction;

public class ExtensionCard extends Card {
    private String name;
    private int faceValue;
    private String description;
    
    private transient CardEffect effect; // Pour le score
    
    // NOUVEAU : La logique pour les Bots
    // Fonction qui prend (StrategyType, Jest) et retourne un Integer (score d'intérêt)
    private transient BiFunction<StrategyType, Jest, Integer> aiHeuristic;

    public ExtensionCard(String name, int faceValue, String description,
                         CardEffect effect,
                         BiFunction<StrategyType, Jest, Integer> aiHeuristic) {
        super(false);
        this.name = name;
        this.faceValue = faceValue;
        this.description = description;
        this.effect = effect;
        this.aiHeuristic = aiHeuristic;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        restoreRuntimeLogic();
    }

    private void restoreRuntimeLogic() {
        for (ExtensionCard ext : ExtensionManager.getAvailableExtensions()) {
            if (ext != null && ext.getName() != null && ext.getName().equals(this.name)) {
                this.effect = ext.getEffect();
                this.aiHeuristic = ext.aiHeuristic;
                return;
            }
        }
        this.effect = new CardEffect() {};
        this.aiHeuristic = null;
    }

    public int getAIValue(StrategyType strategy, Jest currentJest) {
        if (aiHeuristic == null) return 0;
        return aiHeuristic.apply(strategy, currentJest);
    }

    public CardEffect getEffect() { return effect; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    @Override
    public int getFaceValue() { return this.faceValue; }

    @Override
    public int getSuitValue() { return 0; }

    @Override
    public String toString() { return "[" + name + " (Ext)]"; }
}