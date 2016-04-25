package Controllers;

import api.Action;
import api.GameState;

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
    
    @Override
	public String toString() {
    	return getSimpleName();
    }
}
