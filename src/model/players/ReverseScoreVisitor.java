package model.players;


public class ReverseScoreVisitor extends ScoreVisitorImpl {

    @Override
    public int getTotalScore() {
        
        return -super.getTotalScore();
    }
}