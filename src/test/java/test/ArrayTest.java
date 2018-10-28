package test;

import static org.junit.Assert.*;

import org.junit.Test;

import exceptions.MappingClassException;
import mapping.types.Array;
import mapping.types.Textual;


public class ArrayTest {

	@Test
	public void testString() throws Exception {
		final Array<Textual> array = new Array<>();

		array.addValue(new Textual("value1"));
		assertEquals(1 , array.getLength());
		assertEquals("value1" , array.getContentElement(0).toString());
		assertNull(array.getKeySet());
		assertFalse(array.isLeaf());
		
	}
	
	@Test(expected = MappingClassException.class) 
	public void testStringValue() throws Exception { 
		final Array<Textual> array = new Array<>();
		array.getStringValue();
	}
	
	@Test(expected = MappingClassException.class) 
	public void testNumericalValue() throws Exception { 
		final Array<Textual> array = new Array<>();
		array.getNumericalValue();
	}
}
