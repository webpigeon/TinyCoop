package Controllers;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import FastGame.Action;
import gamesrc.GameState;
import javaff.JavaFF;
import javaff.data.Fact;
import javaff.data.GroundFact;
import javaff.data.GroundProblem;
import javaff.data.Plan;
import javaff.data.metric.Metric;
import javaff.data.metric.NamedFunction;
import javaff.search.UnreachableGoalException;

public class FFAgent extends Controller {
	private JavaFF planner;

    public void startGame(int agentID) {
    	
    }

    public Set<javaff.data.Action> buildActions() {
    	
    	
    	Set<javaff.data.Action> actions = new HashSet<javaff.data.Action>();
    	actions.add(new javaff.data.Action("UP"));
    	
    	return actions;
    }
    
    public Action get(GameState game) {
    	
    	try {
        	Set<javaff.data.Action> actions = new HashSet<javaff.data.Action>();
        	Set<Fact> facts = new HashSet<Fact>();
        	GroundFact goal = null;
        	Map<NamedFunction, BigDecimal> f = new TreeMap<NamedFunction, BigDecimal>();
        	Metric m = null;
        	
        	GroundProblem gp = new GroundProblem(actions, facts, goal, f, m);
    		
			Plan plan = planner.plan(gp);
			System.out.println(plan.getActions());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	//Set<Action> a, Set<Fact> i, GroundFact g, Map<NamedFunction, BigDecimal> f, Metric m)
    	
        return Action.NOOP;
    }
	
}
