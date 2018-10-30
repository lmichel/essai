package test;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import mapping.MappingElement;
import parser.LiteMappingParser;
import test.parser.SavotWraper;

public class LiteMappingParserTsmodelFilterTest {
	static LiteMappingParser liteMappingParser;

	@BeforeClass
	static public void setUp() throws Exception {
		URL url = LiteMappingParserTsmodelFilterTest.class.getResource("/test/xml/annot_tsmodel_filter.xml");
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
			assertEquals(85,observables.get(0).getLength() );
			assertEquals(83,observables.get(1).getLength() );
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
		assertEquals("http://svo2.cab.inta-csic.es/theory/fps/fps.php?ID=GAIA/GAIA0.BP",pe.toString() );

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
		assertEquals("1705.9440504175118", t);
		assertEquals("15.64539174200359", mag);
	}

}