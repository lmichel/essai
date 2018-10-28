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
import exceptions.MappingException;
import mapping.MappingElement;
import mapping.types.Array;
import mapping.types.Textual;
import votable.ColumnPattern;
import votable.TableModel;
import votable.VotableModel;
import test.parser.SavotWraper;

public class VotableModelTest {
	public static VotableModel votableModel;

	@BeforeClass
	public static void setUp() throws Exception {
		String sampleName = SavotWraper.class.getResource("/test/xml/gaia_multicol.xml").getFile();;
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
