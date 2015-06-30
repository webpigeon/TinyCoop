package Controllers;

import FastGame.Action;
import FastGame.CoopGame;

/**
 * Created by pwillic on 23/06/2015.
 */
public abstract class Controller {

    public Action get(CoopGame game) {
        return Action.NOOP;
    }
}
