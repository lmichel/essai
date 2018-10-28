package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import mapping.types.Textual;

public class TextualTest {

	@Test
	public void test()throws Exception {
		Textual num= new Textual("12.3");
		assertEquals("12.3", num.getStringValue());
		assertEquals(-1, num.getLength());
		assertNull(num.getKeySet());
		assertTrue(num.isLeaf());
	}
}
