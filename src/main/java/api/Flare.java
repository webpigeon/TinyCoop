package api;

import java.awt.Point;

public class Flare {
	public final Integer pid;
	public final Integer x;
	public final Integer y;
	public final Boolean relative;
	
	public Flare(int pid, int x, int y, boolean relative) {
		this.pid = pid;
		this.x = x;
		this.y = y;
		this.relative = relative;
	}
	
	public Point toAbs(Point base) {
		Point p = new Point(x, y);
		
		if (relative) {
			p.x += base.x;
			p.y += base.y;
		}
		
		return p;
	}
	
	@Override
	public String toString() {
		return String.format("FLARE(%d, %d, %d):%s", pid, x, y, relative?"RELATIVE":"ABSOLUTE");
	}
}
