package Controllers;

import FastGame.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

/**
 * Created by pwillic on 30/06/2015.
 */
public class RandomController extends Controller {

    private int agentID;

    public RandomController(int agentID) {
        this.agentID = agentID;
    }

    @Override
    public Action get(GameState game) {
        return Action.getRandom(agentID, game);
    }
}
