package FastGame;

/**
 * A (very) basic form of communication.
 */
public class TalkAction extends Action {
    public static final Action FLARE_UP = new TalkAction(0, -1, true);
    public static final Action FLARE_DOWN = new TalkAction(0, 1, true);
    public static final Action FLARE_LEFT = new TalkAction(-1, 0, true);
    public static final Action FLARE_RIGHT = new TalkAction(1, 0, true);

	private boolean isBeep;
	private boolean relative;
	
	public TalkAction() {
		super("BEEP", -1, -1);
		this.isBeep = true;
		this.relative = false;
	}
	
	public TalkAction(int x, int y, boolean relative) {
		super("FLARE", x, y);
		this.isBeep = false;
		this.relative = relative;
	}
	
	public boolean isBeep() {
		return isBeep;
	}
	
	public boolean isRelative() {
		return relative;
	}
	
    public boolean isMovement() {
    	return false;
    }

	public boolean isTalk() {
		return true;
	}
	
	public String toString() {
		return String.format("%s - (%d|%d)", super.toString(), getX(), getY());
	}
}
