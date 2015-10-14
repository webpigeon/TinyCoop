package FastGame;

import java.util.Objects;
import java.util.Random;

import gamesrc.GameState;

/**
 * Created by pwillic on 23/06/2015.
 */
public class Action {
    public static final Action UP = new MoveAction("UP", 0, -1);
    public static final Action DOWN = new MoveAction("DOWN", 0, 1);
    public static final Action LEFT = new MoveAction("LEFT", -1, 0);
    public static final Action RIGHT = new MoveAction("RIGHT", 1, 0);
    public static final Action NOOP = new Action("NOOP", 0, 0);
    public static final Action BEEP = new TalkAction(0, 0, 0);
    private static final Random random = new Random();
    //public static final Action[] allActions = {NOOP, UP, DOWN, LEFT, RIGHT};

    private int x, y;
    private final String actionID;
    
    protected Action(String actionID, int x, int y) {
    	this.actionID = actionID;
        this.x = x;
        this.y = y;
    }

    public static Action getRandom(int actionID, GameState state) {
    	Action[] allActions = state.getLegalActions(actionID);
        return allActions[random.nextInt(allActions.length)];
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
        Action action = (Action) o;
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
}
