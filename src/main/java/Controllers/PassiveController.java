package Controllers;

import java.awt.Point;
import java.util.Queue;

import Controllers.astar.PathFind;
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
	private Queue<Action> nextMoves;
	private Point flarePos;
	
    @Override
	public void startGame() {
    	this.nextMoves = null;
    	this.flarePos = null;
	}

	@Override
    public Action get(GameState game) {
    	ObservableGameState gameState = (ObservableGameState)game;
    	
    	Point newFlarePos = gameState.getFlare(0);
    	if (newFlarePos != null) {
    		flarePos = newFlarePos;
    		nextMoves = null;
    	}
    	
    	if (flarePos != null && nextMoves == null) {
    		nextMoves = PathFind.findPath(game, gameState.getPos(0), flarePos);
    	}
    	
    	if (nextMoves == null || nextMoves.isEmpty() ) {
    		return Action.NOOP;
    	} else {
        	Action nextMove = nextMoves.poll();
        	if (nextMoves.isEmpty()) {
        		nextMoves = null;
        		flarePos = null;
        	}
            return nextMove;
    	}
    }
	
}
