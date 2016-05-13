package uk.me.webpigeon.phd.tinycoop.engine.actions;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.ActionType;

public class NoopAction implements Action {

	@Override
	public String getFriendlyName() {
		return "NOOP";
	}

	@Override
	public ActionType getType() {
		return ActionType.NOOP;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public boolean isMovement() {
		return false;
	}

	@Override
	public boolean isNOOP() {
		return true;
	}

	@Override
	public boolean isRelative() {
		return false;
	}

	@Override
	public boolean isTalk() {
		return false;
	}

	@Override
	public String toString() {
		return getFriendlyName();
	}

}
