package FastGame;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNoOpEquals() {
		int x = 0;
		int y = 0;

		FastAction action = new FastAction("TEST", x, y);

		assertEquals(x, action.getX());
		assertEquals(y, action.getY());
	}

}
