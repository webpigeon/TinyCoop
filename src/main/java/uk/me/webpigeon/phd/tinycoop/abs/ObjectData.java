package uk.me.webpigeon.phd.tinycoop.abs;

public class ObjectData {
	public final Integer x;
	public final Integer y;
	public final Integer type;
	public final Integer signal;
	
	public ObjectData(int x, int y, int type, int signal) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.signal = signal;
	}

	public String toString() {
		return String.format("(%d,%d) %d, %d", x, y, type, signal);
	}
	
}
