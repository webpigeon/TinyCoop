package uk.me.webpigeon.phd.tinycoop.api;

public enum ActionType {
	MOVEMENT(true, false, false), NOOP(false, false, true), FLARE(false, true, false);

	private boolean move;
	private boolean noop;
	private boolean comm;

	private ActionType(boolean move, boolean comm, boolean noop) {
		this.move = move;
		this.comm = comm;
		this.noop = noop;
	}

	public boolean isComms() {
		return comm;
	}

	public boolean isMovement() {
		return move;
	}

	public boolean isNoop() {
		return noop;
	}
}
