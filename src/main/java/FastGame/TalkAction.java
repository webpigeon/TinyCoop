package FastGame;

/**
 * A (very) basic form of communication.
 */
public class TalkAction extends Action {

	private boolean isBeep;
	
	public TalkAction() {
		super("BEEP", -1, -1);
		this.isBeep = true;
	}
	
	public TalkAction(int x, int y) {
		super("FLARE", x, y);
		this.isBeep = false;
	}
	
	public boolean isBeep() {
		return isBeep;
	}
	
    public boolean isMovement() {
    	return false;
    }

	public boolean isTalk() {
		return true;
	}
	
	public String toString() {
		return String.format("(%d,%d) - %s", getX(), getY(), super.toString());
	}
}
