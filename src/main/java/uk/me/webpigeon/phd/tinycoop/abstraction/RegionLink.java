package uk.me.webpigeon.phd.tinycoop.abstraction;

public class RegionLink {
	public int top;
	public int bottom;
	public int left;
	public int right;
	
	public int signal;
	
	public String toString() {
		return String.format("[%d]->[%d,%d,%d,%d]",  signal, top, bottom, left, right);
	}
}
