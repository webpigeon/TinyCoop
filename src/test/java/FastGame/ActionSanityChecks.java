package FastGame;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ActionSanityChecks {

	@Parameters(name = "SanityChecks({0},{1})")
	public static Collection<Object[]> generateData() {
		return Arrays.asList(new Object[][] { { 0, 0 }, { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } });
	}

	private final int x;
	private final int y;
	private final FastAction action;

	public ActionSanityChecks(int x, int y) {
		this.x = x;
		this.y = y;
		this.action = new FastAction("TEST", x, y);
	}

	@Test
	public void testEqualsSameVals() {
		FastAction cloneAction = new FastAction("TEST", x, y);

		assertEquals(cloneAction, action);
	}

	@Test
	public void testEqualsSelf() {
		assertEquals(action, action);
	}

	@Test
	public void testGetters() {
		assertEquals(x, action.getX());
		assertEquals(y, action.getY());
	}

}
