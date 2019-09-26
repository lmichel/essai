/**
 * 
 */
package sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.MoreObjects.ToStringHelper;

import mapping.MappingElement;
import parser.LiteMappingParser;

/**
 * This is an example of using the JAVA API to extract data from an annotated VOTable
 * The data extraction only relies on selector based on dmroles
 * 
 * @author laurentmichel
 *
 */
public class ZdfExplorer {

	/**
	 * The file to parse (must be in the classpath)
	 */
	public static final String VOTABLE_RESOURCE = "/test/xml/zdf_annotated.xml";
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
	private SparseCubeReport sparseCubeReport = new SparseCubeReport();

	/**
	 * Contructor: does the data parsing
	 * @throws Exception
	 */
	ZdfExplorer() throws Exception{
		this.initParser();
	}
	
	/**
	 * @throws Exception
	 */
	private void initParser() throws Exception {
		URL url = ZdfExplorer.class.getResource(VOTABLE_RESOURCE);
		String sampleName =url.getFile();
		liteMappingParser = new LiteMappingParser(sampleName);
	}

	/**
	 * Extract data from the DataSet component
	 * @throws Exception
	 */
	public void exploreDataSet() throws Exception{	
		// Getting the DATASET instance
		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:Source.identifier");
		this.sparseCubeReport.sourceId = dataSet.getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("meas:EquatorialPosition.ra");
		this.sparseCubeReport.ra = dataSet.getSubelementsByRole("ivoa:RealQuantity.value").get(0).getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("meas:EquatorialPosition.dec");
		this.sparseCubeReport.dec = dataSet.getSubelementsByRole("ivoa:RealQuantity.value").get(0).getStringValue();
	}
	
	/**
	 * Retrieve the filters used for that TS
	 * @throws Exception
	 */
	public void exploreFilters() throws Exception{
		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:FilterUrl");
		this.sparseCubeReport.filterUrl = dataSet.getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:Filter.name");
		this.sparseCubeReport.filterName = dataSet.getStringValue();
	}
	
	/**
	 * Build a report for each SarseCube
	 * @throws Exception
	 */
	public void exploreData() throws Exception{
		MappingElement data = this.liteMappingParser.getFirstNodeWithRole("nd_point:TimeSeries.points");
		
		MappingElement pointArray = this.liteMappingParser.getFirstNodeWithRole("nd_point:TimeSeries.points");
		this.sparseCubeReport.nbPoints = pointArray.getLength();
		this.sparseCubeReport.columnMapping = pointArray.getColumnRoles();
        for( int i=0 ; i<this.sparseCubeReport.nbPoints ; i++ ){
        	MappingElement row = pointArray.getContentElement(i);
        	this.sparseCubeReport.points.add(
        			new Point(
        					row.getSubelementsByRole("ivoa:RealQuantity.value").get(0).getStringValue(),
        					row.getSubelementsByRole("nd_point:mag.value").get(0).getStringValue()
        					)
        			);
        }

	}
	

	/**
	 * Printout the report all what has been read out of the VOTable
	 */
	public void printReport(){
		// Printing the report
		System.out.println("== Source ==");
		System.out.println("        id:" + this.sparseCubeReport.sourceId);
		System.out.println("  position:" + this.sparseCubeReport.ra + " " + this.sparseCubeReport.dec);
		System.out.println("== Filter ==");
		System.out.println("      name:" + this.sparseCubeReport.filterName);
		System.out.println("       url:" + this.sparseCubeReport.filterUrl);
		System.out.println("== Time Series ==");
		System.out.println("  nb points:" + this.sparseCubeReport.nbPoints);
		System.out.print("col mapping: ");
		for( Entry<Integer, String> entry : this.sparseCubeReport.columnMapping.entrySet()){
			System.out.print(entry.getKey() + "=>" + entry.getValue() + " ");
		}
		System.out.println("");
		for( Point point : this.sparseCubeReport.points){
			System.out.println(point);
		}
	}

	/**
	 * inner class containing values used to describe ons SparseCube
	 * @author laurentmichel
	 */
	class SparseCubeReport{
		String sourceId;
		String ra;
		String dec;
		String filterUrl;
		String filterName;
		int nbPoints;
		List<Point> points = new ArrayList<Point>() ;
		String firstTime;
		String firstMag;
		String lastTime;
		String lastMag;
		Map<Integer, String> columnMapping;
	}
	class Point{
		String ra;
		String dec;
		public Point(String ra, String dec) {
			super();
			this.ra = ra;
			this.dec = dec;
		}
		public String toString(){
			return this.ra + "\t" + this.dec;
		}
		
	}

	/**
	 * Example of the API usage
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ZdfExplorer timeSeriesExample = new ZdfExplorer();
		timeSeriesExample.exploreDataSet();
		timeSeriesExample.exploreFilters();
		timeSeriesExample.exploreData();
		timeSeriesExample.printReport();
	}
	
	/*
	 * # Scale font and line width (dpi) by changing the size! It will always display stretched.
set terminal svg size 400,300 enhanced fname 'arial'  fsize 10 butt solid
set output 'out.svg'

# Key means label...
set key inside bottom right
set xlabel 'Time (HJD)'
set ylabel 'Magnitude (G)'
set title 'Time Series'
set yrange [10:20];
plot  "data.txt"  using 1:2 title 'Time Series' with lines smooth unique

data.txt
# This file is called   force.dat
# Force-Deflection data for a beam and a bar
# Time    Mag G     
2458205.0119003966	17.7203445
2458206.0120819123	17.6563911
2458207.0147988475	17.655283
2458207.0236419393	17.6439056
2458208.0070065474	17.6286964
2458209.010419121	17.4842987
2458210.0052784304	17.4736118
2458211.0110995797	17.6098099

	 */
}
