package test;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mapping.MappingElement;
import mapping.nodes.DataTableCollection;
import parser.LiteMappingParser;
import parser.TemplateModel;
import parser.VodmlBlockParser;
import votable.VotableModel;
import test.parser.Parser;
import test.parser.SavotWraper;

public class LiteMappingParserTsmodelFilterTest {
	static LiteMappingParser liteMappingParser;

	@BeforeClass
	static public void setUp() throws Exception {
		URL url = SavotWraper.class.getResource("/test/xml/annot_tsmodel_filter.xml");
		String sampleName =url.getFile();;
		liteMappingParser = new LiteMappingParser(sampleName);
	}

	@Test
	public void testLocalParsing() throws Exception {
		List<MappingElement> dataid = liteMappingParser.getNodesByRole("ds:dataset.Dataset.dataID");
		assertEquals(1,dataid.size() );
		for(MappingElement element : dataid ){
			assertEquals("Gaia TS Mapping Test",element.getContentElement("ds:dataset.DataID.title").getStringValue() );
			MappingElement contributors = element.getFirstChildByRole("contributors");
			assertEquals(2,contributors.getLength() );
			MappingElement contributor = contributors.getContentElement(0);
			assertEquals("Thank you guy", contributor.getContentElement("ds:dataset.Contributor.acknowledgment").getStringValue());			
		}
	}

	@Test
	public void testSparseCube() throws Exception {
		List<MappingElement> rs = liteMappingParser.getNodesByRole("data");
		assertEquals(1,rs.size() );

		for(MappingElement me: rs ){
			MappingElement dataCollection = me;
			assertEquals(3,dataCollection.getLength() );
			List<MappingElement> observables = dataCollection.getSubelementsByRole("observable");
			assertEquals(3,observables.size() );
			assertEquals(83,observables.get(0).getLength() );
			assertEquals(85,observables.get(1).getLength() );
		}
	}

	@Test
	public void testPoints() throws Exception {

		List<MappingElement> rs = liteMappingParser.getNodesByRole("data");
		assertEquals(1,rs.size() );
		rs = liteMappingParser.getNodesByRole("observable");
		MappingElement me = rs.get(1);
		assertEquals(85, me.getLength() );
		MappingElement point = me.getContentElement(0);
		
		MappingElement timeFrame = point.getOneSubelementByRole("coords:Coordinate.frame");
		assertEquals("TCB", timeFrame.getOneSubelementByRole("coords:domain.time.TimeFrame.timescale").getStringValue() );

		MappingElement pe = point.getOneSubelementByRole("photdm:Access.reference");
		assertEquals("http://svo/filterG",pe.toString() );

		List<MappingElement> mesures = point.getSubelementsByRole("meas:CoordMeasure.coord");
		String t="";
		String mag="";
		assertEquals(2, mesures.size());
		for( MappingElement mes: mesures){
			MappingElement x;
			if( (x = mes.getContentElement("coords:domain.time.JD.date")) != null ) {
				t = x.getStringValue();
			} else if ( (x = mes.getContentElement("ts:Magnitude.value")) != null ) {
				mag = x.getStringValue();
			} 
		}
		assertEquals("1705.9437360200984", t);
		assertEquals("15.216574774452164", mag);
	}

}