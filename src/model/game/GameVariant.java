package model.game;

import model.players.ScoreVisitor;
import java.io.Serializable;

public interface GameVariant extends Serializable {
    
    String getName();

    
    void setup(Game game);

    
    String getRulesDescription();

    
    ScoreVisitor createScoreVisitor();

    
    int getMinPlayers();

    
    int getMaxPlayers();
}
