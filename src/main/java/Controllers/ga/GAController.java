package Controllers.ga;

import Controllers.Controller;
import actions.Action;
import gamesrc.GameState;

/**
 * GA controller for CoopGame, modified from PTSP.
 *
 * Created by jwalto on 01/07/2015.
 */
public class GAController extends Controller {
    private static final Integer GENERATIONS = 10;
    private static final MacroAction NOOP = new MacroAction(Action.NOOP, GAConstants.MACRO_ACTION_LENGTH);

    private GA ga;
    private boolean first;
    private MacroAction currentAction;
    private int macroPos;
    private boolean reset;
    private boolean startOfGame;

    public GAController(boolean first) {
        this.first = first;
        this.currentAction = NOOP;
        this.macroPos = 0;
        this.reset = true;
        this.startOfGame = true;
    }

    @Override
    public Controller getClone() {
        GAController other = new GAController(first);
        return other;
    }

    @Override
    public Action get(GameState game) {
    	if (this.ga == null) {
    		this.ga = new GA(game.getLegalActions(first?0:1));
    	}
    	
        MacroAction nextAction = NOOP;

        if (startOfGame) {
            currentAction = NOOP;
            nextAction = currentAction;
            reset = true;
            macroPos = GAConstants.MACRO_ACTION_LENGTH-1;
            startOfGame = false;
        } else {
            if (macroPos > 0) {
                if (reset) {
                    ga.init(game, first);
                }
                ga.run(game, GENERATIONS, first);
                macroPos--;
            } else if (macroPos == 0) {
                nextAction = currentAction;
                MacroAction gaBestAction = ga.run(game, GENERATIONS, first);
                if (gaBestAction != null) {
                    currentAction = gaBestAction;
                }

                if (currentAction != null) {
                    macroPos = GAConstants.MACRO_ACTION_LENGTH - 1;
                }
                reset = true;
            }
        }


        return nextAction.getAction();
    }
}
