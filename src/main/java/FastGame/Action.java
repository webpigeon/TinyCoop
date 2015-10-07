package FastGame;

import java.util.Objects;
import java.util.Random;

/**
 * Created by pwillic on 23/06/2015.
 */
public final class Action {
    public static final Action UP = new Action(0, -1);
    public static final Action DOWN = new Action(0, 1);
    public static final Action LEFT = new Action(-1, 0);
    public static final Action RIGHT = new Action(1, 0);
    public static final Action NOOP = new Action(0, 0);
    private static final Random random = new Random();
    public static final Action[] allActions = {NOOP, UP, DOWN, LEFT, RIGHT};
    private int x, y;

    protected Action(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Action getRandom() {
        return allActions[random.nextInt(allActions.length)];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public boolean isNoop() {
    	return x==0 && y==0;
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
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        if (this.equals(UP)) return "Action.UP";
        if (this.equals(DOWN)) return "Action.DOWN";
        if (this.equals(LEFT)) return "Action.LEFT";
        if (this.equals(RIGHT)) return "Action.RIGHT";

        return "Action(" + x + ":" + y + ")";
    }
}
