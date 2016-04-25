package actions;

public class NoopAction implements Action {

	@Override
	public ActionType getType() {
		return ActionType.NOOP;
	}

	@Override
	public String getFriendlyName() {
		return "noop";
	}

	@Override
	public boolean isRelative() {
		return false;
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
	public boolean isTalk() {
		return false;
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
	public String toString() {
		return getFriendlyName();
	}
	
}
