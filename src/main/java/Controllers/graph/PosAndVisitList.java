package Controllers.graph;

import java.awt.Point;
import java.util.Arrays;

import api.ObservableGameState;

/**
 * Assume that the world only consists of goals visited and locations
 */
public class PosAndVisitList implements StateAbstraction {
	private Point p1;
	private Point p2;
	private boolean[] p1Visits;
	private boolean[] p2Visits;

	public PosAndVisitList(ObservableGameState state) {
		this.p1 = state.getPos(0);
		this.p2 = state.getPos(1);
		this.p1Visits = new boolean[state.getGoalsCount()];
		this.p2Visits = new boolean[state.getGoalsCount()];

		for (int i = 0; i < state.getGoalsCount(); i++) {
			p1Visits[i] = state.hasVisited(0, i);
			p2Visits[i] = state.hasVisited(1, i);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PosAndVisitList other = (PosAndVisitList) obj;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (!Arrays.equals(p1Visits, other.p1Visits))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		if (!Arrays.equals(p2Visits, other.p2Visits))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + Arrays.hashCode(p1Visits);
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		result = prime * result + Arrays.hashCode(p2Visits);
		return result;
	}

}
