package FastGame;

public class MoveAction extends Action {

	protected MoveAction(String actionID, int x, int y) {
		super(actionID, x, y);
	}

    public boolean isMovement() {
    	return true;
    }
    
	public boolean isTalk() {
		return false;
	}
}
