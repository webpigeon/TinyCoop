package Controllers;

import FastGame.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

/**
 * Created by pwillic on 23/06/2015.
 */
public abstract class Controller {

    public Controller getClone() { return this; }
    
    public void startGame(int agentID) {
    	
    }

    public Action get(GameState game) {
        return Action.NOOP;
    }

    public String getSimpleName(){
        return this.getClass().getSimpleName();
    }
}
