package gamesrc;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import FastGame.Action;
import FastGame.TalkAction;

/**
 * TinyCoop implementation designed for planners.
 * 
 * This implementation puts readablity of the code and exensiablity over speed.
 * It is likely to be much slower than the FastGame implementation so use that
 * one if you want speed.
 */
public class SimpleGame implements ObservableGameState {
	private GameLevel level;
	private boolean[] beeps;
	private Point[] flares;
	private Point[] positions;
	private boolean[] visitList;
	private Map<Integer,Integer> signals;
	
	public SimpleGame(GameLevel level) {
		this.level = level;
		this.beeps = new boolean[level.getPlayerCount()];
		this.flares = new Point[level.getPlayerCount()];
		this.positions = new Point[level.getPlayerCount()];
		this.visitList = new boolean[level.getGoalCount() * level.getPlayerCount()];
		this.signals = new TreeMap<Integer,Integer>();
		
		for (int i=0; i<positions.length; i++) {
			positions[i] = level.getSpawnLocation(i);
		}
	}
	
	public SimpleGame(SimpleGame game) {
		this.level = game.level;
		this.beeps = Arrays.copyOf(game.beeps, game.beeps.length);
		this.flares = Arrays.copyOf(game.flares, game.flares.length);
		this.positions = Arrays.copyOf(game.positions, game.positions.length);
		this.visitList = Arrays.copyOf(game.visitList, game.visitList.length);
		this.signals = new TreeMap<Integer,Integer>(game.signals);
	}

	@Override
	public GameState getClone() {
		return new SimpleGame(this);
	}

	@Override
	public double getScore() {
		double score = 0;
		for (boolean goal : visitList) {
			if (goal) {
				score += 1;
			}
		}
		
		return score/visitList.length;
	}

	@Override
	public boolean hasWon() {
		for (boolean b : visitList) {
			if (!b) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void update(Action p1, Action p2) {
		beeps = new boolean[level.getPlayerCount()];
		flares = new Point[level.getPlayerCount()];
		doAction(0, p1);
		doAction(1, p2);
	}
	
	protected void doAction(int pid, Action action) {
		if (action.isNoop()) {
			return;
		}
		
		Point newPos = new Point(positions[pid]);
		newPos.x = newPos.x + action.getX();
		newPos.y = newPos.y + action.getY();
		
		if (action.isMovement() && level.isWalkable(pid, newPos, this)) {
			level.onStep(this, pid, positions[pid], newPos);
			positions[pid] = newPos;
		}
		
		if (action.isTalk()) {
			if (action.getX() != 0 && action.getY() != 0) {
				flares[pid] = new Point(action.getX(), action.getY());
			} else {
				beeps[pid] = true;
			}
		}
	}

	@Override
	public Point getPos(int i) {
		return new Point(positions[i]);
	}

	@Override
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
	
	@Override
	public void setVisited(int agent, int goalID) {
		visitList[agent * level.getGoalCount() + goalID] = true;
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
		Action[] orginalActions = new Action[]{Action.NOOP, Action.BEEP, Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};
		Action[] actions = new Action[orginalActions.length + level.getWidth()*level.getHeight()];
		
		int i=0;
		for (int x = 0; x < level.getWidth(); x++) {
			for (int y=0; y < level.getHeight(); y++) {
				actions[i++] = new TalkAction(playerID, x, y);
			}
		}
		
		int remaining = actions.length - i;
		for (int j=0; j<remaining; j++) {
			actions[i++] = orginalActions[j];
		}
		
		return actions;
	}

	@Override
	public int getActionLength() {
		return getLegalActions(0).length;
	}

	@Override
	public boolean getBeep(int agent) {
		return beeps[agent];
	}

	@Override
	public Point getFlare(int agent) {
		return flares[agent];
	}

	@Override
	public boolean isWalkable(int pid, int x, int y) {
		return level.isWalkable(pid, new Point(x,y), this);
	}

}
