/**
 * 
 */
package sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mapping.MappingElement;
import parser.LiteMappingParser;

/**
 * This is an example of using the JAVA API to extract data from an annotated VOTable
 * The data extraction only relies on selector based on dmroles
 * 
 * @author laurentmichel
 *
 */
public class TimeSeriesExample {

	/**
	 * The file to parse (must be in the classpath)
	 */
	public static final String VOTABLE_RESOURCE = "/test/xml/annot_tsmodel_filter.xml";
	/**
	 * Instance of the parser providing the API to acces data
	 */
	private LiteMappingParser liteMappingParser;
	/**
	 * Extracted data: dataid tile
	 */
	private String title ="";
	/**
	 * Extracted data: contributor acknowledgement
	 */
	private List<String> contribAck = new ArrayList<>();
	/**
	 * Extracted data: timescale
	 */
	private String timeScale;
	/**
	 * Extracted data: URLs of the photometric filters
	 */
	private List<String> filterUrls = new ArrayList<>();
	/**
	 * Extracted data: List of individual reports for each SpaeseCube
	 * {@link SparseCubeReport} is an internal class
	 */
	private List<SparseCubeReport> sparseCubeReports = new ArrayList<>();

	/**
	 * Contructor: does the data parsing
	 * @throws Exception
	 */
	TimeSeriesExample() throws Exception{
		this.initParser();
	}
	
	/**
	 * @throws Exception
	 */
	private void initParser() throws Exception {
		URL url = TimeSeriesExample.class.getResource(VOTABLE_RESOURCE);
		String sampleName =url.getFile();
		liteMappingParser = new LiteMappingParser(sampleName);
	}

	/**
	 * Extract data from the DataSet component
	 * @throws Exception
	 */
	public void exploreDataSet() throws Exception{	
		// Getting the DATASET instance
		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cube:DataProduct.dataset");
		// Getting the data title
		MappingElement dataid = dataSet.getOneSubelementByRole("ds:dataset.Dataset.dataID");
		this.title = dataid.getContentElement("ds:dataset.DataID.title").toString();
		// Getting the contributor acknowledgments
		MappingElement contributors = this.liteMappingParser.getFirstNodeWithRole("contributors");
		List<MappingElement> ack    = contributors.getSubelementsByRole("ds:dataset.Contributor.acknowledgment");
		this.contribAck = new ArrayList<>();
		for( MappingElement mappingElement: ack){
			this.contribAck.add(mappingElement.getStringValue());
		}
	}
	
	/**
	 * Extract data from the Tilmeframe component
	 * @throws Exception
	 */
	public void exploreTimeframe() throws Exception{
		MappingElement timeFrame = this.liteMappingParser.getFirstNodeWithRole("coords:Coordinate.frame");

		this.timeScale = timeFrame.getOneSubelementByRole("coords:domain.time.TimeFrame.timescale").getStringValue();
	}
	/**
	 * Retrieve the filters used for that TS
	 * @throws Exception
	 */
	public void exploreFilters() throws Exception{
		List<MappingElement> ack = this.liteMappingParser.getNodesByRole("photdm:Access.reference");
		this.filterUrls = new ArrayList<>();
		for( MappingElement mappingElement: ack){
			this.filterUrls.add(mappingElement.getStringValue());
		}
	}
	
	/**
	 * Build a report for each SarseCube
	 * @throws Exception
	 */
	public void exploreData() throws Exception{
		MappingElement data = this.liteMappingParser.getFirstNodeWithRole("data");
		for( MappingElement sparseCube: data.getSubelementsByRole("cube:SparseCube.data")){
			SparseCubeReport sparseCubeReport = new SparseCubeReport();
			this.sparseCubeReports.add(sparseCubeReport);

			MappingElement pointList = sparseCube.getFirstChildByRole("observable");
			sparseCubeReport.nbPoints = pointList.getLength();
			/*
			 * Extract the first photometric point for the report
			 */
			MappingElement firstPoint = pointList.getContentElement(0);		
			List<MappingElement> mesures = firstPoint.getSubelementsByRole("meas:CoordMeasure.coord");
			/*
			 * Measurments of a photometric point are identified by their roles. 
			 * To extract time and mag, we have to read all of them and ti check the roles
			 * This is very inefficient , but this is a consequence of the model design
			 */
			for( MappingElement mes: mesures){
				MappingElement x;
				if( (x = mes.getContentElement("coords:domain.time.JD.date")) != null ) {
					sparseCubeReport.firstTime  = x.getStringValue();
				} else if ( (x = mes.getContentElement("ts:Magnitude.value")) != null ) {
					sparseCubeReport.firstMag = x.getStringValue();
				} 
			}
			/*
			 * Extract the last photometric point for the report
			 */
			MappingElement lastPoint = pointList.getContentElement(sparseCubeReport.nbPoints - 1);
			mesures = lastPoint.getSubelementsByRole("meas:CoordMeasure.coord");
			for( MappingElement mes: mesures){
				MappingElement x;
				if( (x = mes.getContentElement("coords:domain.time.JD.date")) != null ) {
					sparseCubeReport.lastTime  = x.getStringValue();
				} else if ( (x = mes.getContentElement("ts:Magnitude.value")) != null ) {
					sparseCubeReport.lastMag = x.getStringValue();
				} 
			}
			sparseCubeReport.filterUrl = lastPoint.getOneSubelementByRole("photdm:Access.reference").getStringValue();
			
			/*
			 * The Column mapping binds column numbers with DM roles.
			 * The table can be quickly read without taking car on the model.
			 * This might avoid to build an instance for each read row and 
			 * to compare strings to retrieve the role of each table cell
			 * (see comment above)
			 */
			sparseCubeReport.columnMapping = pointList.getColumnRoles();
		}
	}
	

	/**
	 * Printout the report all what has been read out of the VOTable
	 */
	public void printReport(){
		// Printing the report
		System.out.println("Dataset Report");
		System.out.println("    Fitler:");
		System.out.println("        - " + title);
		System.out.println("    Contributors acknwoledgements");
		for( String a: this.contribAck) {
			System.out.println("        - " + a);
		}
		System.out.println("Time Frame");
		System.out.println("    Scale:");
		System.out.println("        - " + this.timeScale);
		System.out.println("Filters");
		System.out.println("    URL:");
		for( String a: this.filterUrls) {
			System.out.println("        - " + a);
		}
		
		System.out.println("Sparse Cubes");
		System.out.println("    nb sparse cubes in the time series:" + this.sparseCubeReports.size());
		for( SparseCubeReport sparseCubeReport: this.sparseCubeReports){
			System.out.println("    Sparse Cube");
			System.out.println("        filter    : " + sparseCubeReport.filterUrl);
			System.out.println("        nb points : " + sparseCubeReport.nbPoints);
			System.out.println("        1st point : " + sparseCubeReport.firstTime + " " + sparseCubeReport.firstMag);
			System.out.println("        last point: " + sparseCubeReport.lastTime + " " + sparseCubeReport.lastMag);
			System.out.println("    column mapping: (col number -> role)");
			for( Entry<Integer, String> entry: sparseCubeReport.columnMapping.entrySet()){
				System.out.println("                    " + entry.getKey() + " -> s" + entry.getValue());
			}
		}
	}

	/**
	 * inner class containing values used to describe ons SparseCube
	 * @author laurentmichel
	 */
	class SparseCubeReport{
		String filterUrl;
		int nbPoints;
		String firstTime;
		String firstMag;
		String lastTime;
		String lastMag;
		Map<Integer, String> columnMapping;
	}
	/**
	 * Example of the API usage
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		TimeSeriesExample timeSeriesExample = new TimeSeriesExample();
		timeSeriesExample.exploreDataSet();
		timeSeriesExample.exploreTimeframe();
		timeSeriesExample.exploreFilters();
		timeSeriesExample.exploreData();
		timeSeriesExample.printReport();
	}
}
