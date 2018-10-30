package test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cds.savot.model.ResourceSet;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;
import exceptions.MissingResourceException;
import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import mapping.MappingElementRef;
import mapping.nodes.DataTableCollection;
import mapping.nodes.Instance;
import mapping.nodes.MultiInstanceCollection;
import mapping.types.Array;
import mapping.types.Textual;
import test.parser.SavotWraper;
import votable.TableModel;


public class MultiInstanceCollectionTest {

	public TableModel tableModel;
	
	
	@Test
	public void testMultipleInsertions() throws Exception { 
		final MultiInstanceCollection<Instance> multiInstanceCollection = new MultiInstanceCollection<>( "id", "role", "name");
		final Instance instance2 = new Instance("id1", "role2", "name");
		final Instance instance3 = new Instance("id2", "role3", "name");
		multiInstanceCollection.addInstance(instance2);
		multiInstanceCollection.addInstance(instance3);
		
		assertEquals("role2", multiInstanceCollection.getContentElement(0).getVodmlRole());
		assertEquals("role3", multiInstanceCollection.getContentElement(1).getVodmlRole());
	}
	@Test
	public void testSearchById() throws Exception { 
		final MultiInstanceCollection<Instance> multiInstanceCollection = new MultiInstanceCollection<>( "id", "role", "name");
		final Instance instance2 = new Instance("id1", "role1", "name");
		final Instance instance3 = new Instance("id2", "role2", "name");
		multiInstanceCollection.addInstance(instance2);
		multiInstanceCollection.addInstance(instance3);
		
		List<MappingElement> sel = multiInstanceCollection.getSubelementsByVodmlId("id2");
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role2", mappingElement.getVodmlRole());
		}
		sel = multiInstanceCollection.getSubelementsByVodmlId("id1");
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role1", mappingElement.getVodmlRole());
		}

		sel = multiInstanceCollection.getSubelementsByVodmlId("id");
		assertEquals(0 , sel.size());
		sel = multiInstanceCollection.getSubelementsByVodmlId("XQHJHJHJK");
		assertEquals(0 , sel.size());

	}
	@Test
	public void testSearchByRole() throws Exception { 
		final MultiInstanceCollection<Instance> multiInstanceCollection = new MultiInstanceCollection<>( "id", "role", "name");
		final Instance instance2 = new Instance("id1", "role1", "name");
		final Instance instance3 = new Instance("id2", "role2", "name");
		multiInstanceCollection.addInstance(instance2);
		multiInstanceCollection.addInstance(instance3);
		
		List<MappingElement> sel = multiInstanceCollection.getSubelementsByRole("role2");
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role2", mappingElement.getVodmlRole());
		}
		sel = multiInstanceCollection.getSubelementsByRole("role1");
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role1", mappingElement.getVodmlRole());
		}

		sel = multiInstanceCollection.getSubelementsByRole("role");
		assertEquals(0 , sel.size());
		sel = multiInstanceCollection.getSubelementsByRole("XQHJHJHJK");
		assertEquals(0 , sel.size());

	}
	@Test
	public void testSearchWithRef() throws Exception { 
		final MultiInstanceCollection<Instance> multiInstanceCollection = new MultiInstanceCollection<>( "id", "role", "name");
		final Instance instance2 = new Instance("id1", "role1", "name");
		final Instance instance3 = new Instance("id2", "role2", "name");
		instance3.setElementRef("ref");
		multiInstanceCollection.addInstance(instance2);
		multiInstanceCollection.addInstance(instance3);
		
		List<MappingElement> sel = multiInstanceCollection.getSubelementsWithRef();
		assertEquals(1 , sel.size());
		for( MappingElement mappingElement: sel){
			assertEquals("role2", mappingElement.getVodmlRole());
		}
	}


}
