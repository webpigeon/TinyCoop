package uk.me.webpigeon.phd.tinycoop.engine.level;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.me.webpigeon.phd.tinycoop.engine.level.Button;

public class ButtonTest {
	private static final int TEST_SIGNAL = 2;
	private Button button;

	@Before
	public void setUp() throws Exception {
		this.button = new Button(TEST_SIGNAL);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSignal() {
		assertEquals(TEST_SIGNAL, button.getSignal());
	}

	@Test
	public void testIsWalkable() {
		assertEquals(true, button.isWalkable(null, 0));
	}

}
