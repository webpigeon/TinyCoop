package uk.me.webpigeon.phd.gvgai;

import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservation;
import uk.me.webpigeon.phd.tinycoop.api.Action;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 13:42
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */

/**
 * Subclass of Player for multi player games.
 * Implements single players act method, returns NULL.
 * Keeps track of playerID and disqualification flag.
 */

public abstract class AbstractMultiPlayer extends Player {

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player. The action returned must be contained in the
     * actions accessible from stateObs.getAvailableActions(), or no action
     * will be applied.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state.
     */
    public Action act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return Action.NOOP;
    }
}