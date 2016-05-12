package uk.me.webpigeon.phd.tinycoop.abs;

import gamesrc.level.ObjectType;

public class ObjectData {
	public final Integer x;
	public final Integer y;
	public final ObjectType type;
	public final Integer signal;
	
	public ObjectData(int x, int y, ObjectType type, int signal) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.signal = signal;
	}

	public String toString() {
		return String.format("(%d,%d)[%s;%d]", x, y, type, signal);
	}
	
}
