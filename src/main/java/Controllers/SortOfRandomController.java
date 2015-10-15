package Controllers;

import java.util.Random;

import FastGame.Action;
import FastGame.CoopGame;
import FastGame.TalkAction;
import gamesrc.GameState;

/**
 * Created by pwillic on 30/06/2015.
 */
public class SortOfRandomController extends Controller {
	private final Double COMM_CHANCE = 0.5;
	
	private Random random;
	private int agentID;

	
	
    @Override
	public void startGame(int agentID) {
		super.startGame(agentID);
    	this.agentID = agentID;
    	this.random = new Random();
	}

	@Override
    public Action get(GameState game) {
    	
		Action[] actions = new Action[]{Action.NOOP, Action.BEEP, Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};
		
		//half of the time replace the actions with communication actions
    	if (random.nextDouble() > COMM_CHANCE) {
    		
    		actions = new Action[game.getWidth() * game.getHeight()];
    		
    		int i=0;
    		for (int x = 0; x < game.getWidth(); x++) {
    			for (int y=0; y < game.getHeight(); y++) {
    				actions[i++] = new TalkAction(agentID, x, y);
    			}
    		}
    		
    	}
    	
		return actions[random.nextInt(actions.length)];

    }
}
