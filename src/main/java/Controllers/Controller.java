package Controllers;

import FastGame.Action;
import FastGame.CoopGame;

/**
 * Created by pwillic on 23/06/2015.
 */
public abstract class Controller {

    public Controller getClone() { return this; }

    public Action get(CoopGame game) {
        return Action.NOOP;
    }

    public String getSimpleName(){
        return this.getClass().getSimpleName();
    }
}