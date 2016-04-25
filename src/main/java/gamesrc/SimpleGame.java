package gamesrc;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.Action;
import api.ActionType;
import api.Flare;
import api.GameObject;
import api.GameState;
import api.ObservableGameState;
import gamesrc.level.GameLevel;

/**
 * TinyCoop implementation designed for planners.
 * 
 * This implementation puts readablity of the code and exensiablity over speed.
 * It is likely to be much slower than the FastGame implementation so use that
 * one if you want speed.
 */
public class SimpleGame implements ObservableGameState {
	private GameLevel level;
	private Flare[] flares;
	private Point[] positions;
	private boolean[] visitList;
	private Map<Integer,Integer> signals;
	
	private boolean hasWon;
	private double score;
	
	public SimpleGame(GameLevel level) {
		this.level = level;
		this.flares = new Flare[level.getPlayerCount()];
		this.positions = new Point[level.getPlayerCount()];
		this.visitList = new boolean[level.getGoalCount() * level.getPlayerCount()];
		this.signals = new HashMap<Integer,Integer>();
		this.hasWon = false;
		this.score = 0;
		
		for (int i=0; i<positions.length; i++) {
			positions[i] = level.getSpawnLocation(i);
		}
	}
	
	public SimpleGame(SimpleGame game) {
		this.level = game.level;
		this.flares = Arrays.copyOf(game.flares, game.flares.length);
		this.positions = Arrays.copyOf(game.positions, game.positions.length);
		this.visitList = Arrays.copyOf(game.visitList, game.visitList.length);
		this.signals = new HashMap<Integer,Integer>(game.signals);
		this.hasWon = game.hasWon;
		this.score = game.score;
	}

	@Override
	public GameState getClone() {
		return new SimpleGame(this);
	}

	@Override
	public double getScore() {		
		return score;
	}

	@Override
	public boolean hasWon() {
		return hasWon;
	}
	
	private void calcuateHasWon() {
		int visits = 0;
		for (boolean b : visitList) {
			if (b) {
				visits++;
			}
		}
		score = visits/(double)visitList.length;
		hasWon = (visits == visitList.length);
	}

	@Override
	public void update(Action p1, Action p2) {
		//reset the com actions
		int playerCount = level.getPlayerCount();
		flares = new Flare[playerCount];
		
		//perform new actions
		doAction(0, p1);
		doAction(1, p2);
	}
	
	protected void doAction(int pid, Action action) {
		if (ActionType.NOOP.equals(action.getType())) {
			return;
		}
		
		if (ActionType.MOVEMENT.equals(action.getType())) {
			Point newPos = new Point(positions[pid]);
			newPos.x = newPos.x + action.getX();
			newPos.y = newPos.y + action.getY();
			
			if (level.isWalkable(pid, newPos, this)) {
				level.onStep(this, pid, positions[pid], newPos);
				positions[pid] = newPos;
			}
		}
		
		if (ActionType.FLARE.equals(action.getType())) {
			flares[pid] = new Flare(pid==0?1:0, action.getX(), action.getY(), action.isRelative());
		}
		
	}

	@Override
	public Point getPos(int i) {
		return new Point(positions[i]);
	}

	public void setSignalState(int signal, boolean state) {
		Integer i = signals.get(signal);
		i = i == null ? 0 : i;
		
		if (state) {
			signals.put(signal, i+1);
		} else {
			signals.put(signal, i-1);
		}
	}

	@Override
	public boolean isSignalHigh(int signal) {
		int signalValue = getSignalState(signal);
		return signalValue >= 1;
	}

	@Override
	public int getSignalState(int signal) {
		Integer i = signals.get(signal);
		return i == null ? 0 : i;
	}
	
	public void setVisited(int agent, int goalID) {
		visitList[agent * level.getGoalCount() + goalID] = true;
		calcuateHasWon();
	}

	@Override
	public boolean hasVisited(int agent, int goalID) {
		return visitList[agent * level.getGoalCount() + goalID];
	}

	@Override
	public int getWidth() {
		return level.getWidth();
	}

	@Override
	public int getHeight() {
		return level.getHeight();
	}

	@Override
	public int getFloor(int x, int y) {
		return level.getFloor(x, y);
	}

	@Override
	public GameObject getObject(int x, int y) {
		return level.getObject(x, y);
	}

	@Override
	public int getGoalsCount() {
		return level.getGoalCount();
	}

	@Override
	public Action[] getLegalActions(int playerID) {
		List<Action> legalActions = level.getLegalMoves();
		
		Action[] actions = new Action[legalActions.size()];
		legalActions.toArray(actions);
		
		return actions;
	}

	@Override
	public int getActionLength() {
		return getLegalActions(0).length;
	}

	@Override
	public Flare getFlare(int agent) {
		return flares[agent];
	}

	@Override
	public boolean isWalkable(int pid, int x, int y) {
		return level.isWalkable(pid, new Point(x,y), this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(flares);
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + Arrays.hashCode(positions);
		result = prime * result + ((signals == null) ? 0 : signals.hashCode());
		result = prime * result + Arrays.hashCode(visitList);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleGame other = (SimpleGame) obj;
		if (!Arrays.equals(flares, other.flares))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (!Arrays.equals(positions, other.positions))
			return false;
		if (signals == null) {
			if (other.signals != null)
				return false;
		} else if (!signals.equals(other.signals))
			return false;
		if (!Arrays.equals(visitList, other.visitList))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s %f", Arrays.toString(positions), score);
	}
	
}
