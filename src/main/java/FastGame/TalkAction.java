package FastGame;

/**
 * A (very) basic form of communication.
 */
public class TalkAction extends FastAction {
	public static final FastAction FLARE_UP = new TalkAction(0, -1, true);
	public static final FastAction FLARE_DOWN = new TalkAction(0, 1, true);
	public static final FastAction FLARE_LEFT = new TalkAction(-1, 0, true);
	public static final FastAction FLARE_RIGHT = new TalkAction(1, 0, true);

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

	@Override
	public boolean isBeep() {
		return isBeep;
	}

	@Override
	public boolean isMovement() {
		return false;
	}

	@Override
	public boolean isRelative() {
		return relative;
	}

	@Override
	public boolean isTalk() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s - (%d|%d)", super.toString(), getX(), getY());
	}
}
