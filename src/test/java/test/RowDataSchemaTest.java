package test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import cds.savot.model.ResourceSet;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;
import mapping.MappingElement;
import mapping.MappingFilter;
import mapping.nodes.Instance;
import mapping.types.Textual;
import votable.RowDataSchema;
import votable.TableModel;
import test.parser.SavotWraper;

public class RowDataSchemaTest {
	public static TableModel tableModel;

	@BeforeClass
	public static void setUp() throws Exception {
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

	public void testField() throws Exception {
		Instance instance = new Instance("root.role");
		instance.addMappingElement("root.role.val1", new Textual("val1"));
		instance.addMappingElement("root.role.val2", new Textual("val2"));
		instance.addMappingElement("root.role.mag", new Textual("@mag"));
		
		Instance instance2 = new Instance("time.role");
		instance.addMappingElement("time.role.val1", new Textual("val1"));
		instance.addMappingElement("time.role.val2", new Textual("val2"));
		instance.addMappingElement("time.role.tome", new Textual("@time"));
		instance.addMappingElement("root.role.tile", instance2);

		RowDataSchema rowDataSchema = new RowDataSchema(instance, tableModel, null);
		assertEquals("rowDataSchema [(4 > root.role.mag) (3 > time.role.tome) ]", rowDataSchema.toString());
	}

	public void testData1() throws Exception{
		Instance instance = new Instance("root.role");
		instance.addMappingElement("root.role.val1", new Textual("val1"));
		instance.addMappingElement("root.role.val2", new Textual("val2"));
		instance.addMappingElement("root.role.mag", new Textual("@mag"));
		
		Instance instance2 = new Instance("time.role");
		instance2.addMappingElement("time.role.val1", new Textual("val1"));
		instance2.addMappingElement("time.role.val2", new Textual("val2"));
		instance2.addMappingElement("time.role.time", new Textual("@time"));
		instance.addMappingElement("root.role.time", instance2);

		Instance row0Instance = tableModel.getRowInstanceAt(0, null);
				
		assertEquals(251, tableModel.getLength(null));

		assertEquals("val1", row0Instance.getContentElement("root.role.val1").getStringValue());
		assertEquals("val2", row0Instance.getContentElement("root.role.val2").getStringValue());
		assertEquals("15.216574774452164", row0Instance.getContentElement("root.role.mag").getStringValue());
		
		MappingElement subInstance = row0Instance.getContentElement("root.role.time");
		
		assertEquals("val1", subInstance.getContentElement("time.role.val1").getStringValue());
		assertEquals("val2", subInstance.getContentElement("time.role.val2").getStringValue());
		assertEquals("1705.9437360200984", subInstance.getContentElement("time.role.time").getStringValue());

		
		row0Instance = this.tableModel.getRowInstanceAt(1, null);
		
		assertEquals("val1", row0Instance.getContentElement("root.role.val1").getStringValue());
		assertEquals("val2", row0Instance.getContentElement("root.role.val2").getStringValue());
		assertEquals("14.767336693604877", row0Instance.getContentElement("root.role.mag").getStringValue());
		subInstance = row0Instance.getContentElement("root.role.time");
		
		assertEquals("val1", subInstance.getContentElement("time.role.val1").getStringValue());
		assertEquals("val2", subInstance.getContentElement("time.role.val2").getStringValue());
		assertEquals("1706.0177100217386", subInstance.getContentElement("time.role.time").getStringValue());
		}
	@Test
	public void testFilter() throws Exception{
		Instance instance = new Instance("root.role");
		instance.addMappingElement("root.role.val1", new Textual("val1"));
		instance.addMappingElement("root.role.val2", new Textual("val2"));
		instance.addMappingElement("root.role.mag", new Textual("@mag"));
		
		Instance instance2 = new Instance("time.role");
		instance2.addMappingElement("time.role.val1", new Textual("val1"));
		instance2.addMappingElement("time.role.val2", new Textual("val2"));
		instance2.addMappingElement("time.role.time", new Textual("@time"));
		instance.addMappingElement("root.role.time", instance2);

		RowDataSchema rowDataSchema = new RowDataSchema(instance, tableModel, new MappingFilter("band", "G"));
		assertEquals(85, tableModel.getLength(rowDataSchema));

		Instance row0Instance = tableModel.getRowInstanceAt(0, rowDataSchema);
				

		assertEquals("val1", row0Instance.getContentElement("root.role.val1").getStringValue());
		assertEquals("val2", row0Instance.getContentElement("root.role.val2").getStringValue());
		assertEquals("15.216574774452164", row0Instance.getContentElement("root.role.mag").getStringValue());
		
		MappingElement subInstance = row0Instance.getContentElement("root.role.time");
		
		assertEquals("val1", subInstance.getContentElement("time.role.val1").getStringValue());
		assertEquals("val2", subInstance.getContentElement("time.role.val2").getStringValue());
		assertEquals("1705.9437360200984", subInstance.getContentElement("time.role.time").getStringValue());

		
		row0Instance = this.tableModel.getRowInstanceAt(1, rowDataSchema);
		
		assertEquals("val1", row0Instance.getContentElement("root.role.val1").getStringValue());
		assertEquals("val2", row0Instance.getContentElement("root.role.val2").getStringValue());
		assertEquals("14.767336693604877", row0Instance.getContentElement("root.role.mag").getStringValue());
		subInstance = row0Instance.getContentElement("root.role.time");
		
		assertEquals("val1", subInstance.getContentElement("time.role.val1").getStringValue());
		assertEquals("val2", subInstance.getContentElement("time.role.val2").getStringValue());
		assertEquals("1706.0177100217386", subInstance.getContentElement("time.role.time").getStringValue());
		}

}
