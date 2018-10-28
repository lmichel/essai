package test;

import static org.junit.Assert.*;

import java.awt.HeadlessException;

import org.junit.Before;
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
import mapping.types.Array;
import mapping.types.Textual;
import votable.ColumnFilter;
import votable.ColumnPattern;
import votable.RowDataSchema;
import votable.TableModel;
import test.parser.SavotWraper;

public class TableModelTest {
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

	@Test
	public void testField() {
		assertEquals("source_id", tableModel.getField(0).getName());
		assertEquals("source_id", tableModel.getFieldByIdOrName("source_id").getName());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testData1() throws Exception{
		assertEquals(251, tableModel.getLength(null));
		Instance instance = tableModel.getRowInstanceAt(0, null);

		assertEquals("5813181197970338560", instance.getContentElement("source_id").getStringValue());
		assertEquals("17091923342681275", instance.getContentElement("transit_id").getStringValue());
		assertEquals("G", instance.getContentElement("band").getStringValue());
		assertEquals("1705.9437360200984", instance.getContentElement("time").getStringValue());
		assertEquals("15.216574774452164", instance.getContentElement("mag").getStringValue());
		assertEquals("15442.456273273616", instance.getContentElement("flux").getStringValue());
		assertEquals("44.151258712309364", instance.getContentElement("flux_error").getStringValue());
		assertEquals("349.76254", instance.getContentElement("flux_over_error").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_photometry").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_variability").getStringValue());
		assertEquals("4097", instance.getContentElement("other_flags").getStringValue());
		assertEquals("369295551293819386", instance.getContentElement("solution_id").getStringValue());
		
		instance = tableModel.getRowInstanceAt(250, null);

		assertEquals("5813181197970338560", instance.getContentElement("source_id").getStringValue());
		assertEquals("51057879547016346", instance.getContentElement("transit_id").getStringValue());
		assertEquals("RP", instance.getContentElement("band").getStringValue());
		assertEquals("2320.203343019354", instance.getContentElement("time").getStringValue());
		assertEquals("14.616872606564277", instance.getContentElement("mag").getStringValue());
		assertEquals("11429.282111731804", instance.getContentElement("flux").getStringValue());
		assertEquals("107.14697456670892", instance.getContentElement("flux_error").getStringValue());
		assertEquals("106.6692", instance.getContentElement("flux_over_error").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_photometry").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_variability").getStringValue());
		assertEquals("0", instance.getContentElement("other_flags").getStringValue());
		assertEquals("369295551293819386", instance.getContentElement("solution_id").getStringValue());

		instance = tableModel.getRowInstanceAt(253, null);

	}


	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testFilter() throws Exception{
		
		RowDataSchema rowDataSchema = new RowDataSchema(null, tableModel, new MappingFilter("band", "bp"));
		assertEquals(83, tableModel.getLength(rowDataSchema));

		Instance instance = tableModel.getRowInstanceAt(0, rowDataSchema);
		assertEquals("5813181197970338560", instance.getContentElement("source_id").getStringValue());
		assertEquals("17091923342681275", instance.getContentElement("transit_id").getStringValue());
		assertEquals("BP", instance.getContentElement("band").getStringValue());
		assertEquals("1705.9440504175118", instance.getContentElement("time").getStringValue());
		assertEquals("15.64539174200359", instance.getContentElement("mag").getStringValue());
		assertEquals("7627.787250948564", instance.getContentElement("flux").getStringValue());
		assertEquals("111.47726591016318", instance.getContentElement("flux_error").getStringValue());
		assertEquals("68.4246", instance.getContentElement("flux_over_error").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_photometry").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_variability").getStringValue());
		assertEquals("0", instance.getContentElement("other_flags").getStringValue());
		assertEquals("369295551293819386", instance.getContentElement("solution_id").getStringValue());
		
		instance = tableModel.getRowInstanceAt(82, rowDataSchema);

		assertEquals("5813181197970338560", instance.getContentElement("source_id").getStringValue());
		assertEquals("51057879547016346", instance.getContentElement("transit_id").getStringValue());
		assertEquals("BP", instance.getContentElement("band").getStringValue());
		assertEquals("2320.2032537866794", instance.getContentElement("time").getStringValue());
		assertEquals("15.349914285198922", instance.getContentElement("mag").getStringValue());
		assertEquals("10013.584205737614", instance.getContentElement("flux").getStringValue());
		assertEquals("101.75432340398386", instance.getContentElement("flux_error").getStringValue());
		assertEquals("98.409424", instance.getContentElement("flux_over_error").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_photometry").getStringValue());
		assertEquals("F", instance.getContentElement("rejected_by_variability").getStringValue());
		assertEquals("0", instance.getContentElement("other_flags").getStringValue());
		assertEquals("369295551293819386", instance.getContentElement("solution_id").getStringValue());

		instance = tableModel.getRowInstanceAt(83, rowDataSchema);

	}


}
