package api;

import gamesrc.actions.NoopAction;

public interface Action {
	public static final Action NOOP = new NoopAction();
	
	public boolean isRelative();
	public int getX();
	public int getY();
	
	ActionType getType();
	
	String getFriendlyName();
	
	public boolean isTalk();
	public boolean isMovement();
	public boolean isNOOP();
	
}
