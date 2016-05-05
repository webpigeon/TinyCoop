package FastGame;

public class MoveAction extends FastAction {

	protected MoveAction(String actionID, int x, int y) {
		super(actionID, x, y);
	}

	@Override
	public boolean isMovement() {
		return true;
	}

	@Override
	public boolean isTalk() {
		return false;
	}
}
