package Controllers;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import Controllers.astar.PathFinder;
import FastGame.Action;
import gamesrc.GameState;
import gamesrc.ObservableGameState;

/**
 * This agent will do nothing unless the other agent uses flare, then it will
 * pathfind to the point they flared.
 * 
 * TinyCoop levels *should* be solvable with this agent if the other agent figures
 * out what is going on (I'm evil and won't tell it ;P).
 */
public class PassiveController extends Controller {
	private static final Action DEFAULT_MOVE = Action.NOOP;
	private Queue<Point> nextMoves;
	private int agentID;
	
    @Override
	public void startGame(int agentID) {
    	this.nextMoves = new LinkedList<>();
    	this.agentID = agentID;
	}

	@Override
    public Action get(GameState game) {
    	ObservableGameState gameState = (ObservableGameState)game;
    	
    	Point flarePos = gameState.getFlare(agentID==0?1:0);
    	if (flarePos != null) {
    		nextMoves = PathFinder.getPath(gameState, gameState.getPos(agentID), flarePos, agentID);
    		if (!nextMoves.isEmpty()) {
    			//System.out.println("I got an instruction and found a path: "+nextMoves);
    		} else {
    			//System.out.println("I got an instruction and could not find a path!");
    		}
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
