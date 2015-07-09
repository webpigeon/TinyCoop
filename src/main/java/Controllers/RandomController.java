package Controllers;

import FastGame.Action;
import FastGame.CoopGame;

/**
 * Created by pwillic on 30/06/2015.
 */
public class RandomController extends Controller {

    @Override
    public Action get(CoopGame game) {
        return Action.getRandom();
    }
}
