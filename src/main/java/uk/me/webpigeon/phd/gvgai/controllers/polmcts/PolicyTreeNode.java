package uk.me.webpigeon.phd.gvgai.controllers.polmcts;

import java.util.Random;

import uk.me.webpigeon.phd.gvgai.Constants;
import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.Utils;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.GVGPolicy;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.controllers.prediction.Policy;

public class PolicyTreeNode
{
	
    private static final double HUGE_NEGATIVE = -10000000.0;
    private static final double HUGE_POSITIVE =  10000000.0;
    public static double epsilon = 1e-6;
    public static double egreedyEpsilon = 0.05;
    public PolicyTreeNode parent;
    public PolicyTreeNode[] children;
    public double totValue;
    public int nVisits;
    public static Random m_rnd;
    public int m_depth;
    protected static double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
	private static GVGPolicy policy;
    public int childIdx;

    public static StateObservationMulti rootState;

    public PolicyTreeNode(Random rnd, GVGPolicy policy) {
        this(null, -1, rnd);
        this.policy = policy;
    }

    public PolicyTreeNode(PolicyTreeNode parent, int childIdx, Random rnd) {
        this.parent = parent;
        this.m_rnd = rnd;
        totValue = 0.0;
        this.childIdx = childIdx;
        if(parent != null)
            m_depth = parent.m_depth+1;
        else
            m_depth = 0;
        children = new PolicyTreeNode[PredictorAgent.NUM_ACTIONS[PredictorAgent.id]];
    }


    public void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;

        int remainingLimit = 5;
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit){
        //while(numIters < Agent.MCTS_ITERATIONS){

            StateObservationMulti state = rootState.copy();

            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            PolicyTreeNode selected = treePolicy(state);
            double delta = selected.rollOut(state);
            backUp(selected, delta);

            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }
        
        System.out.println(numIters);
    }

    public PolicyTreeNode treePolicy(StateObservationMulti state) {

    	PolicyTreeNode cur = this;

        while (!state.isGameOver() && cur.m_depth < PredictorAgent.ROLLOUT_DEPTH)
        {
            if (cur.notFullyExpanded()) {
                return cur.expand(state);

            } else {
            	PolicyTreeNode next = cur.uct(state);
                cur = next;
            }
        }

        return cur;
    }


    public PolicyTreeNode expand(StateObservationMulti state) {

        int bestAction = 0;
        double bestValue = -1;

        for (int i = 0; i < children.length; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        //Roll the state

        //need to provide actions for all players to advance the forward model
        Action[] acts = new Action[PredictorAgent.no_players];

        //set this agent's action
        acts[PredictorAgent.id] = PredictorAgent.actions[PredictorAgent.id][bestAction];
        acts[PredictorAgent.oppID] = policy.getActionAt(acts[PredictorAgent.id], state);

        state.advance(acts);

        PolicyTreeNode tn = new PolicyTreeNode(this,bestAction,this.m_rnd);
        children[bestAction] = tn;
        return tn;
    }

    public double rollOut(StateObservationMulti state)
    {
        int thisDepth = this.m_depth;

        while (!finishRollout(state,thisDepth)) {

            //random move for all players
        	Action[] acts = new Action[PredictorAgent.no_players];
        	
        	if (1 != 1) {
        		acts[PredictorAgent.id] = PredictorAgent.actions[PredictorAgent.id][m_rnd.nextInt(PredictorAgent.NUM_ACTIONS[PredictorAgent.id])];
        		acts[PredictorAgent.oppID] = policy.getActionAt(acts[PredictorAgent.id], state);
        	} else {
        		for (int i = 0; i < PredictorAgent.no_players; i++) {
        			acts[i] = PredictorAgent.actions[i][m_rnd.nextInt(PredictorAgent.NUM_ACTIONS[PredictorAgent.id])];
        		}
        	}
            state.advance(acts);
            thisDepth++;
        }


        double delta = value(state);

        if(delta < bounds[0])
            bounds[0] = delta;
        if(delta > bounds[1])
            bounds[1] = delta;

        //double normDelta = Utils.normalise(delta ,lastBounds[0], lastBounds[1]);

        return delta;
    }
    
    public PolicyTreeNode uct(StateObservationMulti state) {

    	PolicyTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (PolicyTreeNode child : this.children)
        {
            double hvVal = child.totValue;
            double childValue =  hvVal / (child.nVisits + this.epsilon);

            childValue = Utils.normalise(childValue, bounds[0], bounds[1]);
            //System.out.println("norm child value: " + childValue);

            double uctValue = childValue +
                    PredictorAgent.K * Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + this.epsilon));

            uctValue = Utils.noise(uctValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly

            // small sampleRandom numbers: break ties in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null)
        {
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + this.children.length + " " +
            + bounds[0] + " " + bounds[1]);
        }

        //Roll the state:

        //need to provide actions for all players to advance the forward model
        Action[] acts = new Action[PredictorAgent.no_players];

        //set this agent's action
        acts[PredictorAgent.id] = PredictorAgent.actions[PredictorAgent.id][selected.childIdx];

        //get actions available to the opponent and assume they will do a random action
        Action[] oppActions = PredictorAgent.actions[PredictorAgent.oppID];
        acts[PredictorAgent.oppID] = oppActions[new Random().nextInt(oppActions.length)];

        state.advance(acts);

        return selected;
    }
    
    
    public double value(StateObservationMulti a_gameState) {

        boolean gameOver = a_gameState.isGameOver();


        int win = a_gameState.getMultiGameWinner()[PredictorAgent.id];
        double rawScore = a_gameState.getGameScore(PredictorAgent.id);

        if(gameOver && win == Constants.PLAYER_LOSES)
            rawScore += HUGE_NEGATIVE;

        if(gameOver && win == Constants.PLAYER_WINS)
            rawScore += HUGE_POSITIVE;

        return rawScore;
    }

    public boolean finishRollout(StateObservationMulti rollerState, int depth)
    {
        if(depth >= PredictorAgent.ROLLOUT_DEPTH)      //rollout end condition.
            return true;

        if(rollerState.isGameOver())               //end of game
            return true;

        return false;
    }

    public void backUp(PolicyTreeNode node, double result)
    {
    	PolicyTreeNode n = node;
        while(n != null)
        {
            n.nVisits++;
            n.totValue += result;
            n = n.parent;
        }
    }


    public int mostVisitedAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        boolean allEqual = true;
        double first = -1;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null)
            {
                if(first == -1)
                    first = children[i].nVisits;
                else if(first != children[i].nVisits)
                {
                    allEqual = false;
                }

                double childValue = children[i].nVisits;
                childValue = Utils.noise(childValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }else if(allEqual)
        {
            //If all are equal, we opt to choose for the one with the best Q.
            selected = bestAction();
        }
        return selected;
    }

    public int bestAction()
    {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null) {
                //double tieBreaker = m_rnd.nextDouble() * epsilon;
                double childValue = children[i].totValue / (children[i].nVisits + this.epsilon);
                childValue = Utils.noise(childValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1)
        {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return selected;
    }


    public boolean notFullyExpanded() {
        for (PolicyTreeNode tn : children) {
            if (tn == null) {
                return true;
            }
        }

        return false;
    }
}
