package Controllers.astar;

import Controllers.ga.GA;
import FastGame.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

/**
 * Created by jwalto on 02/07/2015.
 */
public class GameNode implements Comparable<GameNode> {
    protected GameState game;
    protected MovePair actions;

    public GameNode(GameState game, MovePair actions) {
        this.game = game;
        this.actions = actions;
    }

    public boolean isTerminal() {
        return game.hasWon() || game.getScore() == 1;
    }

    @Override
    public int compareTo(GameNode o) {
        return Double.compare(o.game.getScore(), game.getScore());
    }

    public boolean gameEquals(GameState game2, GameState game3) {
//        if (game2.getScore() != game3.getScore()) return false;
//        if (!game2.getPos(0).equals(game3.getPos(0))) return false;
//        if (!game2.getPos(1).equals(game3.getPos(1))) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameNode gameNode = (GameNode) o;

        if (!actions.equals(gameNode.actions)) return false;
        if (!gameEquals(game, gameNode.game)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = game.hashCode();
        result = 31 * result + actions.hashCode();
        return result;
    }

    public String toString() {
        return null;
//        return actions.toString()+" "+game.getScore()+",P1{"+game.getPos(0)+"},P2{"+game.getPos(1)+"}";
    }
}
