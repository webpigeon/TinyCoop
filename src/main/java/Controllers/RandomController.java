package Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import FastGame.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

/**
 * Created by pwillic on 30/06/2015.
 */
public class RandomController extends Controller {
	private Random random;
	
	public RandomController() {
		this.random = new Random();
	}
	
	public RandomController(long seed) {
		this.random = new Random(seed);
	}
	
    @Override
    public Action get(GameState game) {
    	List<Action> actions = game.getLegalActions();
    	int r = random.nextInt(actions.size());
        return actions.get(r);
    }

}
