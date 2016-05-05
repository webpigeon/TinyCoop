package api;

import gamesrc.actions.NoopAction;

public interface Action {
	public static final Action NOOP = new NoopAction();

	String getFriendlyName();

	ActionType getType();

	public int getX();

	public int getY();

	public boolean isMovement();

	public boolean isNOOP();

	public boolean isRelative();

	public boolean isTalk();

}
