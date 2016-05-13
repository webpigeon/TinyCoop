package uk.me.webpigeon.phd.tinycoop.abstraction;

import uk.me.webpigeon.phd.tinycoop.engine.level.ObjectType;

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
