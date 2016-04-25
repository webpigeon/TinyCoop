package Controllers;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import Controllers.astar.PathFinder;
import actions.Action;
import gamesrc.Flare;
import gamesrc.GameState;
import gamesrc.ObservableGameState;

/**
 * This agent will do nothing unless the other agent uses flare, then it will
 * pathfind to the point they flared.
 * 
 * This version will repathfind every tick until a flare 
 * 
 * TinyCoop levels *should* be solvable with this agent if the other agent figures
 * out what is going on (I'm evil and won't tell it ;P).
 */
public class PassiveRefindController extends Controller {
	private static final Action DEFAULT_MOVE = Action.NOOP;
	private Queue<Point> nextMoves;
	private int agentID;
	private Point flarePos;
	
    @Override
	public void startGame(int agentID) {
    	this.nextMoves = new LinkedList<>();
    	this.agentID = agentID;
	}

	@Override
    public Action get(GameState game) {
    	ObservableGameState gameState = (ObservableGameState)game;
    	
    	Flare flare = gameState.getFlare(agentID==0?1:0);
    	if (flare != null) {
    		Point flarePos = flare.toAbs(gameState.getPos(flare.pid));
    		this.flarePos = flarePos;
    		//System.out.println("got flared: "+flarePos);
    	}
    	
    	
    	if (this.flarePos != null) {
    		nextMoves = PathFinder.getPath(gameState, gameState.getPos(agentID), this.flarePos, agentID);
    		nextMoves.poll();
    		//System.out.println("nextMoves: "+nextMoves);
    	}
    	
		Action nextMove = DEFAULT_MOVE;
    	if ( !nextMoves.isEmpty() ) {
    		
        	Point nextMovePoint = nextMoves.peek();
        	if (!gameState.isWalkable(agentID, nextMovePoint.x, nextMovePoint.y)) {
        		return nextMove;
        	} else {
        		nextMoves.poll();
        	}
        	
        	
        	for (Action action : gameState.getLegalActions(agentID)) {
        		
        		Point actionResult = gameState.getPos(agentID);
        		if (action.isMovement()) {
        			actionResult.x += action.getX();
        			actionResult.y += action.getY();
        			
            		if (actionResult.equals(nextMovePoint)) {
            			//System.out.println("I am going to move now! "+action);
            			nextMove = action;
            		}
        		}
        		
        	}
        	
    	}
    	
    	return nextMove;
    }
	
}
