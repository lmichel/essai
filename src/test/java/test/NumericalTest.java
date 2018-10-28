package test;

import static org.junit.Assert.*;

import org.junit.Test;

import mapping.types.Numerical;

public class NumericalTest {

	@Test
	public void test()throws Exception {
		Numerical num= new Numerical(12.3);
		assertEquals(12.3, num.getNumericalValue(), 0.01);
		assertEquals("12.3", num.getStringValue());
		assertEquals(-1, num.getLength());
		assertNull(num.getKeySet());
		assertTrue(num.isLeaf());
	}
	

	@Test
	public void notNum() throws Exception{ 
		new Numerical("qqqqq");
		boolean isNan = Double.isNaN( (new Numerical("qqqqq")).getNumericalValue());
		assertTrue(isNan);

 
	}

}
