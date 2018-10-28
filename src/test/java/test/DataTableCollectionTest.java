package test;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
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
import mapping.nodes.DataTableCollection;
import mapping.nodes.Instance;
import mapping.types.Array;
import mapping.types.Textual;
import votable.TableModel;
import test.parser.SavotWraper;


public class DataTableCollectionTest {

	public static TableModel tableModel;

	@BeforeClass
	public static  void setUp() throws Exception {
		String sampleName = SavotWraper.class.getResource("/test/xml/gaia_multicol.xml").getFile();;
		SavotPullParser parser = new SavotPullParser(sampleName, SavotPullEngine.FULLREAD, false);
		SavotVOTable voTable = parser.getVOTable();
		voTable.getResources();
		ResourceSet resourcseSet = voTable.getResources();      	
		for( SavotResource savotResource: resourcseSet.getItems() ) {
			for(  SavotTable table: savotResource.getTables().getItems()){
				tableModel = new  TableModel(table);
				return;
			}
		}
	}

	
	
	@Test(expected = UnconsistantResourceException.class)
	public void testMultipleInsertions() throws Exception { 
		final DataTableCollection array = new DataTableCollection( "id", "role", "name");
		final Instance instance2 = new Instance("id", "role2", "name");
		final Instance instance3 = new Instance("id", "role3", "name");
		array.addValue(instance2);
		array.addValue(instance3);
	}

	@Test(expected = UnconsistantResourceException.class)
	public void  testFailingColumnPattern () throws Exception { 
		final DataTableCollection dataTableCollection = new DataTableCollection( "id", "role", "name");
		final Instance instance2 = new Instance("id", "role2", "name");
		dataTableCollection.addValue(instance2);
		dataTableCollection.addValue(instance2);
	}


	@Test
	public void  testDataAccess () throws Exception { 
		final DataTableCollection dataTableCollection = new DataTableCollection( "id", "role", "name");
		Instance instance = new Instance("i-id", "i-role2", "i-name");
		instance.addMappingElement("time", new Textual("@time"));
		instance.addMappingElement("magnitude", new Textual("@mag"));
		dataTableCollection.addValue(instance);
		dataTableCollection.setValuesFromTableModel(tableModel);
		assertEquals(251, dataTableCollection.getLength() );
		assertEquals("INSTANCE id=i-id role=i-role2\ntime=1705.9437360200984 magnitude=15.216574774452164"
				, dataTableCollection.getContentElement(0).toString().trim());
		assertEquals("INSTANCE id=i-id role=i-role2\ntime=2320.203343019354 magnitude=14.616872606564277"
				, dataTableCollection.getContentElement(250).toString().trim() );
	}
	@Test
	public void  testFilteredDataAccess () throws Exception { 
		final DataTableCollection dataTableCollection = new DataTableCollection( "id", "role", "name");
		Instance instance = new Instance("i-id", "i-role2", "i-name");
		instance.addMappingElement("time", new Textual("@time"));
		instance.addMappingElement("mag", new Textual("@mag"));
		instance.addMappingElement("band", new Textual("@band"));
		dataTableCollection.addValue(instance);
		dataTableCollection.setMappingFilter("band", "G");
		dataTableCollection.setValuesFromTableModel(tableModel);
		assertEquals(85, dataTableCollection.getLength() );
		assertEquals("INSTANCE id=i-id role=i-role2\ntime=1705.9437360200984 mag=15.216574774452164 band=G", dataTableCollection.getContentElement(0).toString().trim());
		assertEquals("INSTANCE id=i-id role=i-role2\ntime=2320.202939518489 mag=15.067951819188966 band=G", dataTableCollection.getContentElement(84).toString().trim() );
		dataTableCollection.suppressMappingFilter();
	}
	
	@Test
	public void  testBadFilteredDataAccess () throws Exception { 
		DataTableCollection dataTableCollection = new DataTableCollection( "id", "role", "name");
		Instance instance = new Instance("i-id", "i-role2", "i-name");
		instance.addMappingElement("time", new Textual("@time"));
		instance.addMappingElement("mag", new Textual("@mag"));
		instance.addMappingElement("band", new Textual("@band"));
		dataTableCollection.addValue(instance);
		dataTableCollection.setMappingFilter("band", "GGGG");
		dataTableCollection.setValuesFromTableModel(tableModel);
		assertEquals(0, dataTableCollection.getLength() );
		
		dataTableCollection = new DataTableCollection( "id", "role", "name");
		instance = new Instance("i-id", "i-role2", "i-name");
		instance.addMappingElement("time", new Textual("@time"));
		instance.addMappingElement("mag", new Textual("@mag"));
		instance.addMappingElement("band", new Textual("@band"));
		dataTableCollection.addValue(instance);
		dataTableCollection.setValuesFromTableModel(tableModel);
		assertEquals(251, dataTableCollection.getLength() );
		dataTableCollection.suppressMappingFilter();

	}

	@Test
	public void  testGroupedDataAccess () throws Exception { 
		DataTableCollection dataTableCollection = new DataTableCollection( "id", "role", "name");
		Instance instance = new Instance("i-id", "i-role2", "i-name");
		instance.addMappingElement("time", new Textual("@time"));
		instance.addMappingElement("mag", new Textual("@mag"));
		instance.addMappingElement("band", new Textual("@band"));
		dataTableCollection.addValue(instance);
		dataTableCollection.setValuesFromTableModel(tableModel);
		
		Map<String, Array> groupedData = dataTableCollection.groupContentByColumnValue("band");
		assertEquals(3, groupedData.size() );
		
		
		int length = groupedData.get("G").getLength();
		assertEquals(85, length );
		for( int i=0 ; i<length ; i++ ){
			assertEquals("G", groupedData.get("G").getContentElement(i).getContentElement("band").toString());
		}
		length = groupedData.get("BP").getLength();
		assertEquals(83, length );
		for( int i=0 ; i<length ; i++ ){
			assertEquals("BP", groupedData.get("BP").getContentElement(i).getContentElement("band").toString());
		}
		
		length = groupedData.get("RP").getLength();
		assertEquals(83, length );
		for( int i=0 ; i<length ; i++ ){
			assertEquals("RP", groupedData.get("RP").getContentElement(i).getContentElement("band").toString());
		}
	}


}
