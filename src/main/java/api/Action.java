package api;

import actions.NoopAction;
import actions.relative.MovePosition;
import actions.relative.RelativeFlare;

public interface Action {

    public static final Action MOVE_UP = new MovePosition("MOVE_UP", 0, -1);
    public static final Action MOVE_DOWN = new MovePosition("MOVE_DOWN", 0, 1);
    public static final Action MOVE_LEFT = new MovePosition("MOVE_LEFT", -1, 0);
    public static final Action MOVE_RIGHT = new MovePosition("MOVE_RIGHT", 1, 0);
    public static final Action FLARE_UP = new RelativeFlare("FLARE_UP", 0, -1);
    public static final Action FLARE_DOWN = new RelativeFlare("FLARE_DOWN", 0, 1);
    public static final Action FLARE_LEFT = new RelativeFlare("FLARE_LEFT", -1, 0);
    public static final Action FLARE_RIGHT = new RelativeFlare("FLARE_RIGHT", 1, 0);
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
