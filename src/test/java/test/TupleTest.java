package test;

import static org.junit.Assert.*;

import org.junit.Test;

import exceptions.MappingClassException;
import mapping.types.Array;
import mapping.types.Textual;
import mapping.types.Tuple;


public class TupleTest {

	@Test
	public void testString() throws Exception {
		final Tuple tuple = new Tuple();
		
		tuple.addMappingElement("key1", new Textual("value1"));
		assertEquals(1 , tuple.getKeySet().size());
		assertEquals("value1" , tuple.getContentElement("key1").toString());
		assertEquals(1, tuple.getLength());
		assertFalse(tuple.isLeaf());
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
	
	@Test
	public void testReplace() throws Exception { 
		final Tuple tupleOrg = new Tuple();		
		tupleOrg.addMappingElement("keyOrg", new Textual("valueOrg"));
		final Tuple tupleEnd = new Tuple();		
		tupleEnd.addMappingElement("keyEnd", new Textual("valueEnd"));
		
		final Tuple tuple = new Tuple();		
		tuple.addMappingElement("key", tupleOrg);
		tuple.replaceElement(tupleOrg, tupleEnd);
		assertEquals("keyEnd=valueEnd", tuple.getContentElement("key").toString().trim());


	}

	


}
