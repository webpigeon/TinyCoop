package actions;

public abstract class AbstractAction implements Action {

	private final String name;
	private final ActionType type;
	private int x;
	private int y;
	
	public AbstractAction(String name, int x, int y, ActionType type) {
		this.name = name;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String getFriendlyName() {
		return name;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%d,%d)", getFriendlyName(), getX(), getY());
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public ActionType getType() {
		return type;
	}

	@Override
	public boolean isTalk() {
		return type.isComms();
	}

	@Override
	public boolean isMovement() {
		return type.isMovement();
	}

	@Override
	public boolean isNOOP() {
		return type.isNoop();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractAction))
			return false;
		AbstractAction other = (AbstractAction) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
