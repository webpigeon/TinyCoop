package FastGame;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ActionSanityChecks {
	
	@Parameters(name="SanityChecks({0},{1})")
	public static Collection<Object[]> generateData() {
		return Arrays.asList(new Object[][]{
			{0, 0},
			{-1, 0},
			{1, 0},
			{0, -1},
			{0, 1}
		});
	}
	
	private final int x;
	private final int y;
	private final Action action;
	
	public ActionSanityChecks(int x, int y) {
		this.x = x;
		this.y = y;
		this.action = new Action("TEST", x, y);
	}
	
	@Test
	public void testGetters() {
		assertEquals(x, action.getX());
		assertEquals(y, action.getY());
	}
	
	@Test
	public void testEqualsSelf() {
		assertEquals(action, action);
	}
	
	@Test
	public void testEqualsSameVals() {
		Action cloneAction = new Action(x, y);
		
		assertEquals(cloneAction, action);
	}
	

}
