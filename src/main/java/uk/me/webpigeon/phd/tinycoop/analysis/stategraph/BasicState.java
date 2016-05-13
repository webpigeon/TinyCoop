package uk.me.webpigeon.phd.tinycoop.analysis.stategraph;

import java.awt.Point;

import uk.me.webpigeon.phd.tinycoop.api.ObservableGameState;

/**
 * Assume that the world only consists of scores and locations
 */
public class BasicState implements StateAbstraction {
	private Point p1;
	private Point p2;
	private double score;

	public BasicState(ObservableGameState state) {
		this.p1 = state.getPos(0);
		this.p2 = state.getPos(1);
		this.score = state.getScore();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicState other = (BasicState) obj;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

}
