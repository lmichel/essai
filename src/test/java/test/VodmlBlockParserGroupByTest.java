package test;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import parser.TemplateModel;
import parser.VodmlBlockParser;
import test.parser.Parser;
import votable.VotableModel;

public class VodmlBlockParserGroupByTest {
	VotableModel votableModel;
	VodmlBlockParser treeBuilder;

	@Before
	public void setUp() throws Exception {
		String fileName = "/test/xml/annot_multiband_groupby.xml";
		URL url = VodmlBlockParserGroupByTest.class.getResource(fileName);
		String sampleName =url.getFile();;
		this.votableModel = new VotableModel(sampleName);
		treeBuilder = new VodmlBlockParser(Parser.class.getResourceAsStream(fileName));
		treeBuilder.parse();

	}

	@Test (expected=UnconsistantResourceException.class)
	public void testProv() throws Exception {
		System.out.println("====================");

		for( Entry<String, TemplateModel> entry : this.treeBuilder.templates.entrySet()) {
			System.out.println("====================");
			try {
			entry.getValue().setValuesFromTableModel(votableModel);
			} catch (Exception e) {
e.printStackTrace();			}

			for( MappingElement mappingElemnt: entry.getValue().getMappingElements() ) {
				List<MappingElement> rs = mappingElemnt.getSubelementsByRole("timeseries:dataset.DataSet.calib_level");
				for(MappingElement element : rs ){
					assertEquals("4",element.getStringValue() );
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
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=1705.9441391177577 timeseries:dataset.point.mag=14.76056670662418 timeseries:dataset.point.band=RP", points.getContentElement(0).toString().trim());
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2006.2250143236568 timeseries:dataset.point.mag=14.709081878676741 timeseries:dataset.point.band=RP", points.getContentElement(19).toString().trim());
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2164.560576942061 timeseries:dataset.point.mag=14.768907118194697 timeseries:dataset.point.band=RP", points.getContentElement(65).toString().trim());
					assertEquals("INSTANCE id=no-id role=timeseries:data.Point\ntimeseries:dataset.point.time=2320.203343019354 timeseries:dataset.point.mag=14.616872606564277 timeseries:dataset.point.band=RP", points.getContentElement(82).toString().trim());
				}
				
			}
		}
	}



}
