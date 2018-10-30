package test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import exceptions.MappingException;
import votable.ColumnPattern;
import votable.VotableModel;

public class VotableModelTest {
	public static VotableModel votableModel;

	@BeforeClass
	public static void setUp() throws Exception {
		String sampleName = VotableModelTest.class.getResource("/test/xml/gaia_multicol.xml").getFile();;
		votableModel = new VotableModel(sampleName);
	}

	@Test
	public void testField() throws MappingException {
		assertEquals("source_id", votableModel.getField("_table1", 0).getName());
		assertEquals("source_id", votableModel.getFieldByIdOrName("_table1", "source_id").getName());
	}

	@Test
	public void testData1() throws Exception {
		ColumnPattern pattern = new ColumnPattern();
		pattern.addColumnRef(0);
		pattern.addColumnRef(1);
		pattern.addColumnRef(8);
		pattern.addColumnRef(9);

		assertEquals(251, votableModel.getLength("_table1"));
		assertEquals("5813181197970338560", votableModel.getColumnValueAt("_table1", 0, 0));
		assertEquals("17091923342681275", votableModel.getColumnValueAt("_table1", 0, 1));
		assertEquals("G", votableModel.getColumnValueAt("_table1", 0, 2));
		assertEquals("1705.9437360200984", votableModel.getColumnValueAt("_table1", 0, 3));
	}



}
