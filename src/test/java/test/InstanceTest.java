package test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import exceptions.MappingClassException;
import mapping.MappingElement;
import mapping.nodes.Instance;
import mapping.types.Array;
import mapping.types.Textual;
import mapping.types.Tuple;


public class InstanceTest {

	@Test
	public void testString() throws Exception{
		final Instance instance = new Instance("id", "role", "name");
		
		instance.addMappingElement("key1", new Textual("value1"));
		assertEquals(1 , instance.getKeySet().size());
		assertEquals("value1" , instance.getContentElement("key1").toString());
		assertEquals(-1, instance.getLength());
		assertFalse(instance.isLeaf());
	}
	
	@Test(expected = MappingClassException.class) 
	public void testStringValue() throws Exception{ 
		final Instance instance = new Instance("id", "role", "name");
		instance.getStringValue();
	}
	
	@Test(expected = MappingClassException.class) 
	public void testNumericalValue() throws Exception{ 
		final Instance instance = new Instance("id", "role", "name");
		instance.getNumericalValue();
	}
	
	@Test
	public void testGetChildByRole() throws Exception{ 
		final Instance instance1 = new Instance("id", "role1", "name");
		final Instance instance2 = new Instance("id", "role2", "name");
		final Instance instance3 = new Instance("id", "role3", "name");
		instance1.addMappingElement("key1", instance2);
		instance1.addMappingElement("key2", instance3);

		MappingElement sel = instance1.getFirstChildByRole("role2");
		assertNotNull(sel);
		assertEquals("role2" , sel.getNodeId().role);
		
		sel = instance1.getFirstChildByRole("role3");
		assertNotNull(sel);
		assertEquals("role3" , sel.getNodeId().role);

		sel = instance1.getFirstChildByRole("roleXXX");
		assertNull(sel);
	}
	
	@Test
	public void testSearchByVodmlId() throws Exception {
		final Instance instance1 = new Instance("id1", "role1", "name");
		final Instance instance2 = new Instance("id2", "role2", "name");
		final Instance instance3 = new Instance("id3", "role3", "name");
		instance1.addMappingElement("key1", instance2);
		instance1.addMappingElement("key2", instance3);
		
		List<MappingElement> sel = instance1.getSubelementsByVodmlId("id2");
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role2", mappingElement.getVodmlRole());
		}
		sel = instance1.getSubelementsByVodmlId("id3");
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role3", mappingElement.getVodmlRole());
		}
		sel = instance1.getSubelementsByVodmlId("id1");
		assertEquals(0 , sel.size());
		sel = instance1.getSubelementsByVodmlId("XXX");
		assertEquals(0 , sel.size());
		
	}

	

}
