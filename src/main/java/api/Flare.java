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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		result = prime * result + ((relative == null) ? 0 : relative.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		Flare other = (Flare) obj;
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		if (relative == null) {
			if (other.relative != null)
				return false;
		} else if (!relative.equals(other.relative))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("FLARE(%d, %d, %d):%s", pid, x, y, relative?"RELATIVE":"ABSOLUTE");
	}
}
