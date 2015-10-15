package FastGame;

/**
 * A (very) basic form of communication.
 */
public class TalkAction extends Action {

	private int signal;
	private boolean isBeep;
	
	public TalkAction(int signal) {
		super("BEEP", -1, -1);
		this.signal = signal;
		this.isBeep = true;
	}
	
	public TalkAction(int signal, int x, int y) {
		super("FLARE", x, y);
		this.signal = signal;
		this.isBeep = false;
	}
	
	public int getSignal() {
		return signal;
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
}
