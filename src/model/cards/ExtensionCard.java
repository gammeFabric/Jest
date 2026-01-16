package model.cards;

import model.players.Jest;
import model.players.strategies.StrategyType;

import model.game.ExtensionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.BiFunction;

/**
 * Représente une carte d'extension personnalisée.
 * 
 * <p>Les cartes d'extension permettent d'ajouter de la variété au jeu
 * avec des effets spéciaux et des mécaniques de score alternatives.</p>
 * 
 * <p><b>Caractéristiques :</b></p>
 * <ul>
 *   <li>Nom et description personnalisés</li>
 *   <li>Valeur de base</li>
 *   <li>Effet de score via {@link CardEffect}</li>
 *   <li>Heuristique d'évaluation pour les IA</li>
 * </ul>
 * 
 * <p><b>Exemples de cartes d'extension :</b></p>
 * <ul>
 *   <li><b>The Shield</b> - Annule les pénalités de Carreau/Cœur</li>
 *   <li><b>The Crown</b> - +5 points directs</li>
 *   <li><b>The Jester</b> - +10 avec Joker, -5 sinon</li>
 *   <li><b>The Spy</b> - Carte neutre à 2 points</li>
 * </ul>
 * 
 * <p><b>Restauration après désérialisation :</b> Les effets et heuristiques
 * sont reconstruits depuis {@link model.game.ExtensionManager}.</p>
 * 
 * @see model.cards.CardEffect
 * @see model.game.ExtensionManager
 */
public class ExtensionCard extends Card {
    private String name;
    private int faceValue;
    private String description;

    private transient CardEffect effect;

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
        this.effect = new CardEffect() {
        };
        this.aiHeuristic = null;
    }

    public int getAIValue(StrategyType strategy, Jest currentJest) {
        if (aiHeuristic == null)
            return 0;
        return aiHeuristic.apply(strategy, currentJest);
    }

    public CardEffect getEffect() {
        return effect;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getFaceValue() {
        return this.faceValue;
    }

    @Override
    public int getSuitValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "[" + name + " (Ext)]";
    }
}