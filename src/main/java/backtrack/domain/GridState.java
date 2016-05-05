package backtrack.domain;

import java.util.ArrayList;
import java.util.Collection;

import backtrack.State;

public class GridState implements State {
	private int x;
	private int y;

	public GridState(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		GridState other = (GridState) object;
		return x == other.x && y == other.y;
	}

	@Override
	public Collection<State> expand() {

		Collection<State> children = new ArrayList<State>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}

				if (x + i > 20 || x + i < -20 || y + j < 20 || y + j > -20) {
					children.add(new GridState(x + i, y + j));
				}
			}
		}

		return children;
	}

	@Override
	public Integer getCost() {
		return 1;
	}

	@Override
	public int hashCode() {
		return x + y;
	}

	@Override
	public String toString() {
		return String.format("(%d,%d)", x, y);
	}

}
