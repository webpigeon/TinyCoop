package uk.me.webpigeon.phd.tinycoop.api;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FlareEqualityTests {
	
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
			{0, 0, 0, false},
			{0, 0, 1, false},
			{0, 1, 1, false},
			{1, 1, 1, false}
		});
	}
	
	private int pid;
	private int x;
	private int y;
	private boolean relative;
	
	public FlareEqualityTests(int pid, int x, int y, boolean relative) {
		this.pid = pid;
		this.x = x;
		this.y = y;
		this.relative = relative;
	}

	@Test
	public void test() {
		Flare f1 = new Flare(pid, x, y, relative);
		Flare f2 = new Flare(pid, x, y, relative);
		
		assertEquals(f1, f2);
	}

}
