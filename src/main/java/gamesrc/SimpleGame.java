package gamesrc;

import java.awt.Point;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import api.Action;
import api.ActionType;
import api.Flare;
import api.GameObject;
import api.ObservableGameState;
import api.controller.GameObservation;
import gamesrc.controllers.SimpleGameObservation;
import gamesrc.level.GameLevel;

/**
 * TinyCoop implementation designed for planners.
 *
 * This implementation puts readablity of the code and exensiablity over speed.
 * It is likely to be much slower than the FastGame implementation so use that
 * one if you want speed.
 */
public class SimpleGame implements ObservableGameState {
	private static final Integer NUM_SIGNALS = 5;
	private final GameLevel level;
	private final int[] signals;
	private final Point[] positions;
	private final BitSet visitList;
	private Flare[] flares;

	private int goals;
	private boolean hasWon;
	private double score;

	public SimpleGame(GameLevel level) {
		this.level = level;
		this.flares = new Flare[level.getPlayerCount()];
		this.positions = new Point[level.getPlayerCount()];
		this.visitList = new BitSet(level.getGoalCount() * level.getPlayerCount());
		this.goals = level.getGoalCount() * level.getPlayerCount();
		this.signals = new int[NUM_SIGNALS];
		this.hasWon = false;
		this.score = 0;

		for (int i = 0; i < positions.length; i++) {
			positions[i] = level.getSpawnLocation(i);
		}
	}

	public SimpleGame(SimpleGame game) {
		this.level = game.level;
		this.flares = Arrays.copyOf(game.flares, game.flares.length);
		this.positions = Arrays.copyOf(game.positions, game.positions.length);
		this.visitList = (BitSet) game.visitList.clone();
		this.signals = Arrays.copyOf(game.signals, game.signals.length);

		this.goals = game.goals;
		this.hasWon = game.hasWon;
		this.score = game.score;
	}

	private void calcuateHasWon() {
		int visits = visitList.cardinality();
		score = visits / (double) goals;
		hasWon = (visits == goals);
	}

	protected void doAction(int pid, Action action) {
		assert action != null : "passing null actions is not permitted";
		ActionType type = action.getType();

		// If it was a NOOP or null, don't do anything
		if (type == null || type.equals(ActionType.NOOP)) {
			return;
		}

		// deal with movement actions
		if (type.equals(ActionType.MOVEMENT)) {
			Point newPos = new Point(positions[pid]);
			newPos.x = newPos.x + action.getX();
			newPos.y = newPos.y + action.getY();

			if (level.isWalkable(pid, newPos, this)) {
				level.onStep(this, pid, positions[pid], newPos);
				positions[pid] = newPos;
			}
			return;
		}

		// now, flare actions
		if (type.equals(ActionType.FLARE)) {
			flares[pid] = new Flare(pid == 0 ? 1 : 0, action.getX(), action.getY(), action.isRelative());
			return;
		}

		assert false : "got unknown action type " + type + ", not sure what to do";
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
		} else if (!level.equals(other.level)) {
			// System.out.println("levels were different");
			return false;
		}
		if (!Arrays.equals(positions, other.positions)) {
			// System.out.println("positions were different");
			return false;
		}
		if (!Arrays.equals(signals, other.signals)) {
			// System.out.println("visit list was different")
			return false;
		}
		if (!visitList.equals(other.visitList)) {
			// System.out.println("visit list was different");
			return false;
		}
		return true;
	}

	@Override
	public int getActionLength() {
		return level.getLegalMoves().size();
	}

	@Override
	public SimpleGame getClone() {
		return new SimpleGame(this);
	}

	@Override
	public Flare getFlare(int agent) {
		return flares[agent];
	}

	@Override
	public int getFloor(int x, int y) {
		return level.getFloor(x, y);
	}

	@Override
	public int getGoalsCount() {
		return level.getGoalCount();
	}

	@Override
	public int getHeight() {
		return level.getHeight();
	}

	@Override
	public List<Action> getLegalActions(int playerID) {
		List<Action> legalActions = level.getLegalMoves();
		return Collections.unmodifiableList(legalActions);
	}

	@Override
	public GameObject getObject(int x, int y) {
		return level.getObject(x, y);
	}

	public GameObservation getObservationFor(int agentID) {
		return new SimpleGameObservation(getClone(), agentID);
	}

	@Override
	public Point getPos(int i) {
		return new Point(positions[i]);
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public int getSignalState(int signal) {
		return signals[signal];
	}

	@Override
	public int getWidth() {
		return level.getWidth();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(flares);
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + Arrays.hashCode(positions);
		result = prime * result + Arrays.hashCode(signals);
		result = prime * result + visitList.hashCode();
		return result;
	}

	@Override
	public boolean hasVisited(int agent, int goalID) {
		return visitList.get(agent * level.getGoalCount() + goalID);
	}

	@Override
	public boolean hasWon() {
		return hasWon;
	}

	@Override
	public boolean isSignalHigh(int signal) {
		int signalValue = getSignalState(signal);
		return signalValue >= 1;
	}

	@Override
	public boolean isWalkable(int pid, int x, int y) {
		return level.isWalkable(pid, new Point(x, y), this);
	}

	public void setSignalState(int signal, boolean state) {
		assert signal > signals.length;
		signals[signal] += state ? +1 : -1;
	}

	public void setVisited(int agent, int goalID) {
		visitList.set(agent * level.getGoalCount() + goalID);
		calcuateHasWon();
	}

	@Override
	public String toString() {
		return String.format("%s %f", Arrays.toString(positions), score);
	}

	@Override
	public void update(Action p1, Action p2) {
		// reset the com actions
		int playerCount = level.getPlayerCount();
		flares = new Flare[playerCount];

		// perform new actions
		doAction(0, p1);
		doAction(1, p2);
	}

}
