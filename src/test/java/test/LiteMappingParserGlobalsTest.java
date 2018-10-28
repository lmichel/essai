package test;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import mapping.MappingElement;
import mapping.nodes.DataTableCollection;
import parser.LiteMappingParser;
import parser.TemplateModel;
import parser.VodmlBlockParser;
import votable.VotableModel;
import test.parser.Parser;
import test.parser.SavotWraper;

public class LiteMappingParserGlobalsTest {
	LiteMappingParser liteMappingParser;

	@Before
	public void setUp() throws Exception {
		URL url = SavotWraper.class.getResource("/test/xml/annot_multiband_globals.xml");
		String sampleName =url.getFile();;
		this.liteMappingParser = new LiteMappingParser(sampleName);
	}

	@Test
	public void testLocalParsing() throws Exception {
		List<MappingElement> rs = this.liteMappingParser.getNodesByRole("timeseries:dataset.DataSet.calib_level");
		for(MappingElement element : rs ){
			assertEquals("3",element.getStringValue() );
		}
		rs = this.liteMappingParser.getNodesByRole("timeseries:dataset.DataSet.creator");
		for(MappingElement element : rs ){
			assertEquals("MySelf",element.getStringValue() );
		}
		rs = this.liteMappingParser.getNodesByRole("timeseries:dataset.DataSet.contributor");
		for(MappingElement element : rs ){
			assertEquals("MySelf",element.getStringValue() );
		}
		rs = this.liteMappingParser.getNodesByRole("timeseries:dataset.DataSet.publisher_did");
		for(MappingElement element : rs ){
			assertEquals("ivoa://pouet",element.getStringValue() );
		}
		rs = this.liteMappingParser.getNodesByRole("timeseries:TimeSerie.points");
		for(MappingElement points : rs ){
			assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=1705.9437360200984 timeseries:dataset.point.mag=15.216574774452164 timeseries:dataset.point.band=G", points.getContentElement(0).toString().trim());
			assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=1705.9440504175118 timeseries:dataset.point.mag=15.64539174200359 timeseries:dataset.point.band=BP", points.getContentElement(85).toString().trim());
			assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2009.4031123033421 timeseries:dataset.point.mag=14.783508622583406 timeseries:dataset.point.band=RP", points.getContentElement(208).toString().trim());
			assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2320.203343019354 timeseries:dataset.point.mag=14.616872606564277 timeseries:dataset.point.band=RP", points.getContentElement(250).toString().trim());
		}
	}

	@Test
	public void testCrossRef() throws Exception {
		List<MappingElement> rs = this.liteMappingParser.getNodesByRole("ds:authors");
		assertEquals(1,rs.size() );
		MappingElement authors = rs.iterator().next();
		assertEquals(2,authors.getLength() );

		for( String key: authors.getContentElement(0).getKeySet()){
			assertEquals("ds:author.name", key);
			break;
		}
		for( String key: authors.getContentElement(1).getKeySet()){
			assertEquals("ds:author.name", key);
			break;
		}
		assertEquals("Cresitello",authors.getContentElement(1).getContentElement("ds:author.name").toString());
		assertEquals("Mark",authors.getContentElement(1).getContentElement("ds:author.firstname").toString());
		assertEquals("Michel",authors.getContentElement(0).getContentElement("ds:author.name").toString());
		assertEquals("Laurent",authors.getContentElement(0).getContentElement("ds:author.firstname").toString());
	}



}