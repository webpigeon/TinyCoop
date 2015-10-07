package gamesrc;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import FastGame.Action;

/**
 * TinyCoop implementation designed for planners.
 * 
 * This implementation puts readablity of the code and exensiablity over speed.
 * It is likely to be much slower than the FastGame implementation so use that
 * one if you want speed.
 */
public class SimpleGame implements ObservableGameState {
	private GameLevel level;
	private Point[] positions;
	private boolean[] visitList;
	private Set<Integer> signals;
	
	public SimpleGame(GameLevel level) {
		this.level = level;
		this.positions = new Point[level.getPlayerCount()];
		this.visitList = new boolean[level.getGoalCount() * level.getPlayerCount()];
		this.signals = new HashSet<Integer>();
		
		for (int i=0; i<positions.length; i++) {
			positions[i] = level.getSpawnLocation(i);
		}
	}
	
	public SimpleGame(SimpleGame game) {
		this.level = game.level;
		this.positions = Arrays.copyOf(game.positions, game.positions.length);
		this.visitList = Arrays.copyOf(game.visitList, game.visitList.length);
		this.signals = new HashSet<Integer>();
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
		doAction(0, p1);
		doAction(1, p2);		
	}
	
	protected void doAction(int pid, Action action) {
		Point newPos = new Point(positions[pid]);
		newPos.x = newPos.x + action.getX();
		newPos.y = newPos.y + action.getY();
		if (!action.isNoop() && level.isWalkable(pid, newPos, this)) {
			level.onStep(this, pid, positions[pid], newPos);
			positions[pid] = newPos;
		}
	}

	@Override
	public Point getPos(int i) {
		return positions[i];
	}

	@Override
	public void setSignalState(int signal, boolean state) {
		if (state) {
			signals.add(signal);
		} else {
			signals.remove(signal);
		}
		System.out.println(signals);
	}

	@Override
	public boolean isSignalHigh(int signal) {
		return signals.contains(signal);
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

}
