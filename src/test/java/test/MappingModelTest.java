package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import mapping.MappingElement;
import mapping.MappingModel;
import mapping.nodes.Instance;


public class MappingModelTest {

	@Test
	public void testSelection()throws Exception { 
		final Instance instance1 = new Instance("id", "role1", "name");
		final Instance instance2 = new Instance("id", "role2", "name");
		final Instance instance3 = new Instance("id", "role3", "name");
		final MappingModel root = new MappingModel(instance1);
		
		instance1.addMappingElement("key1", instance2);
		instance2.addMappingElement("key2", instance3);

		MappingElement sel = root.getNode("role1", "role2", "role3");
		
		assertNotNull(sel);
		assertEquals("role3" , sel.getNodeId().role);
		
        sel = root.getNode("role1", "role2XX", "role3");		
		assertNull(sel);
		
	}
	

}
