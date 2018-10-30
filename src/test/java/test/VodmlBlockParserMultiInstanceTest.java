package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import mapping.MappingElement;
import parser.TemplateModel;
import parser.VodmlBlockParser;
import test.parser.Parser;
import votable.VotableModel;

public class VodmlBlockParserMultiInstanceTest {
	VotableModel votableModel;
	VodmlBlockParser treeBuilder;

	@Before
	public void setUp() throws Exception {
		String fileName = "/test/xml/annot_nontable_coll.xml";
		URL url = VodmlBlockParserMultiInstanceTest.class.getResource(fileName);
		String sampleName =url.getFile();;
		this.votableModel = new VotableModel(sampleName);
		treeBuilder = new VodmlBlockParser(Parser.class.getResourceAsStream(fileName));
		treeBuilder.parse();

	}

	@Test
	public void testProv() throws Exception {
		for( Entry<String, TemplateModel> entry : this.treeBuilder.templates.entrySet()) {
			for( MappingElement mappingElemnt: entry.getValue().getMappingElements() ) {
				
				mappingElemnt.setValuesFromTableModel(this.votableModel.getTable(entry.getKey()));
				List<MappingElement> rs = mappingElemnt.getSubelementsByRole("timeseries:dataset.DataSet.calib_level");
				for(MappingElement element : rs ){
					assertEquals("3",element.getStringValue() );
				}
				mappingElemnt.setValuesFromTableModel(this.votableModel.getTable(entry.getKey()));
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
	public void testNonTableColl() throws Exception {
		for( Entry<String, TemplateModel> entry : this.treeBuilder.templates.entrySet()) {
			for( MappingElement mappingElemnt: entry.getValue().getMappingElements() ) {
				
				mappingElemnt.setValuesFromTableModel(this.votableModel.getTable(entry.getKey()));
				List<MappingElement> rs = mappingElemnt.getSubelementsByRole("timeseries:TimeSerie.author");
				assertEquals(2, rs.size());
				int cpt=0;
				for(MappingElement points : rs ){
					assertTrue(points.getContentElement("timeseries:TimeSerie.author.name").toString().matches("(Michel|Cresitello)"));
					assertTrue(points.getContentElement("timeseries:TimeSerie.author.firstname").toString().matches("(Laurent|Mark)"));
					cpt++;
				}
			}
		}
	}



}
