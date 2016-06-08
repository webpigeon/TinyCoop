package uk.me.webpigeon.phd.gvgai.controllers.polmcts;

import java.util.List;
import java.util.Random;

import uk.me.webpigeon.phd.gvgai.AbstractMultiPlayer;
import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.GVGPolicy;
import uk.me.webpigeon.phd.tinycoop.api.Action;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class PredictorAgent extends AbstractMultiPlayer {

    public static int[] NUM_ACTIONS;
    public static int MCTS_ITERATIONS = 100;
    public static int ROLLOUT_DEPTH = 250;
    public static double K = Math.sqrt(2);
    public static double REWARD_DISCOUNT = 1.00;
    public static Action[][] actions;
    public static int id, oppID, no_players;

    protected SingleMCTSPlayer mctsPlayer;
    protected GVGPolicy policy;

    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public PredictorAgent(StateObservationMulti so, ElapsedCpuTimer elapsedTimer, int playerID, GVGPolicy policy)
    {
        //get game information

        no_players = so.getNoPlayers();
        id = playerID;
        oppID = (id + 1) % so.getNoPlayers();

        //Get the actions for all players in a static array.

        NUM_ACTIONS = new int[no_players];
        actions = new Action[no_players][];
        for (int i = 0; i < no_players; i++) {

            List<Action> act = so.getAvailableActions(i);

            actions[i] = new Action[act.size()];
            for (int j = 0; j < act.size(); ++j) {
                actions[i][j] = act.get(j);
            }
            NUM_ACTIONS[i] = actions[i].length;
        }

        //Create the player.
        this.policy = policy;
        mctsPlayer = getPlayer(so, elapsedTimer);
    }
    
    

    @Override
	public void setup(String actionFile, int randomSeed, boolean isHuman) {
		super.setup(actionFile, randomSeed, isHuman);
		policy.init(oppID, id);
	}



	public SingleMCTSPlayer getPlayer(StateObservationMulti so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random(), policy);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Action act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);

        //... and return it.
        return actions[id][action];
    }

    
    public String toString() {
    	return "OLMCTS_PREDICTOR["+policy.toString()+"]";
    }
    
}
