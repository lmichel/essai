package test;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import mapping.MappingElement;
import parser.LiteMappingParser;
import test.parser.SavotWraper;

public class LiteMappingParserTsmodelTest {
	static LiteMappingParser liteMappingParser;

	@BeforeClass
	static public void LiteMappingParserTsmodelTest() throws Exception {
		URL url = SavotWraper.class.getResource("/test/xml/annot_tsmodel_nofilter.xml");
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
			assertEquals(1,dataCollection.getLength() );
			List<MappingElement> observables = dataCollection.getSubelementsByRole("observable");
			assertEquals(1,observables.size() );
			for(MappingElement observable: observables ){
				assertEquals(251,observable.getLength() );
			}

		}
	}
	
	@Test
	public void testPoints() throws Exception {

		List<MappingElement> rs = liteMappingParser.getNodesByRole("data");
		assertEquals(1,rs.size() );
		rs = liteMappingParser.getNodesByRole("observable");

		for(MappingElement me: rs ){
			assertEquals(251,me.getLength() );
			MappingElement point = me.getContentElement(0);
			MappingElement pe =  point.getOneSubelementByRole("coords:domain.time.TimeFrame.timescale");
			assertEquals("TT",pe.toString() );
			
			pe = point.getOneSubelementByRole("photdm:Access.reference");
			assertEquals("http://svo/filterG",pe.toString() );
			
			pe = point.getOneSubelementByRole("coords:domain.time.JD.date");
			assertEquals("1705.9437360200984",pe.toString() );
			
			pe = point.getOneSubelementByRole("ts:Magnitude.value");
			assertEquals("15.216574774452164",pe.toString() );
			
		}
	}

}