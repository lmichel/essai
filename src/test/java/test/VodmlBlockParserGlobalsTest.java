package test;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import mapping.MappingElement;
import parser.TemplateModel;
import parser.VodmlBlockParser;
import test.parser.Parser;
import votable.VotableModel;

public class VodmlBlockParserGlobalsTest {
	VotableModel votableModel;
	VodmlBlockParser vodmlBlockParser;

	@Before
	public void setUp() throws Exception {
		URL url = VodmlBlockParserGlobalsTest.class.getResource("/test/xml/annot_multiband_globals.xml");
		String sampleName =url.getFile();;
		this.votableModel = new VotableModel(sampleName);
		vodmlBlockParser = new VodmlBlockParser(Parser.class.getResourceAsStream("/test/xml/annot_multiband_globals.xml"));
		vodmlBlockParser.parse();

	}

	@Test
	public void testTemplateParsing() throws Exception {
		for( Entry<String, TemplateModel> entry : this.vodmlBlockParser.templates.entrySet()) {
			if( entry.getKey().equalsIgnoreCase("globals")){
				continue;
			}
			entry.getValue().setValuesFromTableModel(votableModel);
			for( MappingElement mappingElemnt: entry.getValue().getMappingElements() ) {
				//mappingElemnt.resolveValueByReference(this.votableModel.getTable(entry.getKey()));
				List<MappingElement> rs = mappingElemnt.getSubelementsByRole("timeseries:dataset.DataSet.calib_level");
				for(MappingElement element : rs ){
					assertEquals("3",element.getStringValue() );
				}
				//mappingElemnt.resolveValueByReference(this.votableModel.getTable(entry.getKey()));
				rs = mappingElemnt.getSubelementsByRole("timeseries:dataset.DataSet.creator");
				for(MappingElement element : rs ){
					assertEquals("MySelf",element.getStringValue() );
				}
				rs = mappingElemnt.getSubelementsByRole("timeseries:dataset.DataSet.contributor");
				for(MappingElement element : rs ){
					assertEquals("MySelf",element.getStringValue() );
				}
				rs = mappingElemnt.getSubelementsByRole("timeseries:dataset.DataSet.publisher_did");
				for(MappingElement element : rs ){
					assertEquals("ivoa://pouet",element.getStringValue() );
				}


				rs = mappingElemnt.getSubelementsByRole("timeseries:TimeSerie.points");
				for(MappingElement points : rs ){
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=1705.9437360200984 timeseries:dataset.point.mag=15.216574774452164 timeseries:dataset.point.band=G", points.getContentElement(0).toString().trim());
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=1705.9440504175118 timeseries:dataset.point.mag=15.64539174200359 timeseries:dataset.point.band=BP", points.getContentElement(85).toString().trim());
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2009.4031123033421 timeseries:dataset.point.mag=14.783508622583406 timeseries:dataset.point.band=RP", points.getContentElement(208).toString().trim());
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2320.203343019354 timeseries:dataset.point.mag=14.616872606564277 timeseries:dataset.point.band=RP", points.getContentElement(250).toString().trim());
				}
			}

		}
	}	

	@Test
	public void testGlobalsParsing() throws Exception {
		for( Entry<String, TemplateModel> entry : this.vodmlBlockParser.templates.entrySet()) {
			if( entry.getKey().equalsIgnoreCase("globals")){
				TemplateModel templateModel = entry.getValue();
				List<MappingElement> rs = templateModel.getElementsByRole("ds:author");
				assertEquals(2,rs.size() );
				rs = templateModel.getElementsByRole("ds:author.name");
				assertEquals(2,rs.size() );
				rs = templateModel.getElementsByRole("ds:author.firstname");
				assertEquals(2,rs.size() );
				
				rs = templateModel.getElementsById("auth2");
				assertEquals(1,rs.size() );
				
				rs = templateModel.getElementsById("authXX");
				assertEquals(0,rs.size() );
			}
		}
	}
	
	@Test
	public void testGlobalsRefLookup() throws Exception {
		for( Entry<String, TemplateModel> entry : this.vodmlBlockParser.templates.entrySet()) {
			if( !entry.getKey().equalsIgnoreCase("globals")){
				TemplateModel templateModel = entry.getValue();
				Set<MappingElement> rs = templateModel.getElementsWithRef();
				assertEquals(2,rs.size() );
			}
		}
	}

}