package uk.me.webpigeon.phd.tinycoop.api;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class FlareDisequalCheck {

	@Test
	public void testDifferentPID() {
		int x = 0;
		int y = 0;
		boolean relative = false;
		
		Flare f1 = new Flare(0, x, y, relative);
		Flare f2 = new Flare(1, x, y, relative);
		
		assertThat(f1, not(f2));
	}
	
	@Test
	public void testDifferentX() {
		int pid = 0;
		int y = 0;
		boolean relative = false;
		
		Flare f1 = new Flare(pid, 0, y, relative);
		Flare f2 = new Flare(pid, 1, y, relative);
		
		assertThat(f1, not(f2));
	}
	
	@Test
	public void testDifferentY() {
		int pid = 0;
		int x = 0;
		boolean relative = false;
		
		Flare f1 = new Flare(pid, x, 0, relative);
		Flare f2 = new Flare(pid, x, 1, relative);
		
		assertThat(f1, not(f2));
	}

	@Test
	public void testDifferentRelative() {
		int pid = 0;
		int x = 0;
		int y = 0;
		
		Flare f1 = new Flare(pid, x, y, true);
		Flare f2 = new Flare(pid, x, y, false);
		
		assertThat(f1, not(f2));
	}
	
}
