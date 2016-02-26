package gamesrc.experiment;

public class GameSetup {
	public String p1;
	public String p2;
	public String levelID;
	public String actionSet;
	
	public String toString() {
		return String.format("%s and %s on %s (%s)", p1, p2, levelID, actionSet);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionSet == null) ? 0 : actionSet.hashCode());
		result = prime * result + ((levelID == null) ? 0 : levelID.hashCode());
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameSetup other = (GameSetup) obj;
		if (actionSet == null) {
			if (other.actionSet != null)
				return false;
		} else if (!actionSet.equals(other.actionSet))
			return false;
		if (levelID == null) {
			if (other.levelID != null)
				return false;
		} else if (!levelID.equals(other.levelID))
			return false;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
	}
	
	
}
