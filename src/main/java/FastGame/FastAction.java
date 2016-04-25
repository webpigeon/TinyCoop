package FastGame;

import java.awt.Point;
import java.util.Objects;
import java.util.Random;

import actions.Action;
import actions.ActionType;
import gamesrc.GameState;
import gamesrc.SimpleGame;

/**
 * Created by pwillic on 23/06/2015.
 */
public class FastAction implements Action {
    public static final FastAction UP = new MoveAction("UP", 0, -1);
    public static final FastAction DOWN = new MoveAction("DOWN", 0, 1);
    public static final FastAction LEFT = new MoveAction("LEFT", -1, 0);
    public static final FastAction RIGHT = new MoveAction("RIGHT", 1, 0);
    public static final FastAction NOOP = new FastAction("NOOP", 0, 0);
    public static final FastAction BEEP = new TalkAction();
    //public static final Action[] allActions = {NOOP, UP, DOWN, LEFT, RIGHT};

    private int x, y;
    private final String actionID;
    
    protected FastAction(String actionID, int x, int y) {
    	this.actionID = actionID;
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public boolean isMovement() {
    	return true;
    }
    
    public boolean isNoop() {
    	return "NOOP".equals(actionID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastAction action = (FastAction) o;
        return Objects.equals(getX(), action.getX()) &&
                Objects.equals(getY(), action.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionID, getX(), getY());
    }

    @Override
    public String toString() {
    	return actionID;
    }

	public boolean isTalk() {
		return false;
	}
	
	//are these relative to a game object?
	public boolean isRelative() {
		return true;
	}
	
	public boolean isBeep() {
		return false;
	}

	@Override
	public ActionType getType() {
		if ("NOOP".equals(actionID)) {
			return ActionType.NOOP;
		}
		return ActionType.MOVEMENT;
	}

	@Override
	public String getFriendlyName() {
		return actionID;
	}

	@Override
	public boolean isNOOP() {
		return false;
	}
	
}
