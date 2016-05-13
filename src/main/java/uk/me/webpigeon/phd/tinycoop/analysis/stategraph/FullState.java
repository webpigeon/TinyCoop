package uk.me.webpigeon.phd.tinycoop.analysis.stategraph;

import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class FullState implements StateAbstraction {
	private final GameState state;

	public FullState(GameState state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FullState other = (FullState) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

}
