package Controllers.astar;

import api.Action;

/**
 * Created by jwalto on 02/07/2015.
 */
public class MovePair {
	protected Action p1Move;
	protected Action p2Move;

	public MovePair(Action p1, Action p2) {
		this.p1Move = p1;
		this.p2Move = p2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MovePair movePair = (MovePair) o;

		if (!p1Move.equals(movePair.p1Move))
			return false;
		if (!p2Move.equals(movePair.p2Move))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = p1Move.hashCode();
		result = 31 * result + p2Move.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", p1Move, p2Move);
	}
}
