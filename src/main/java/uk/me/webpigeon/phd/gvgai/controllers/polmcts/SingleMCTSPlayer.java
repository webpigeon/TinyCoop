package uk.me.webpigeon.phd.gvgai.controllers.polmcts;

import java.util.Random;

import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.GVGPolicy;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.NestedControllerPolicy;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.OneStepLookAhead;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 07/11/13
 * Time: 17:13
 */
public class SingleMCTSPlayer
{


    /**
     * Root of the tree.
     */
    public PolicyTreeNode m_root;
    public GVGPolicy policy;

    /**
     * Random generator.
     */
    public Random m_rnd;


    public SingleMCTSPlayer(Random a_rnd, GVGPolicy policy)
    {
        m_rnd = a_rnd;
        this.policy = policy;
    }

    /**
     * Inits the tree with the new observation state in the root.
     * @param a_gameState current state of the game.
     */
    public void init(StateObservationMulti a_gameState)
    {
        //Set the game observation to a newly root node.
        //System.out.println("learning_style = " + learning_style);
        m_root = new PolicyTreeNode(m_rnd, policy);
        m_root.rootState = a_gameState;
    }

    /**
     * Runs MCTS to decide the action to take. It does not reset the tree.
     * @param elapsedTimer Timer when the action returned is due.
     * @return the action to execute in the game.
     */
    public int run(ElapsedCpuTimer elapsedTimer)
    {
        //Do the search within the available time.
        m_root.mctsSearch(elapsedTimer);

        //Determine the best action to take and return it.
        int action = m_root.mostVisitedAction();
        //int action = m_root.bestAction();
        return action;
    }

}