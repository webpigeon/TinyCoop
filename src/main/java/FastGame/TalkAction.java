package FastGame;

/**
 * A (very) basic form of communication.
 */
public class TalkAction extends Action {

	private int signal;
	
	public TalkAction(int signal) {
		super("BEEP", 0, 0);
		this.signal = signal;
	}
	
	public TalkAction(int signal, int x, int y) {
		super("FLARE", x, y);
		this.signal = signal;
	}
	
	public int getSignal() {
		return signal;
	}
	
    public boolean isMovement() {
    	return false;
    }
	
	public boolean isTalk() {
		return true;
	}
}
