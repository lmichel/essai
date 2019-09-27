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
 * The data extraction only relies on selectors based on dmroles
 * Developed in the frame of the CAB_MSD proposal
 * 
 * @author laurentmichel
 *
 */
public class ZtfExplorer {

	/**
	 * The file to parse (must be in the classpath)
	 */
	public static final String VOTABLE_RESOURCE = "/test/xml/ztf_annotated.xml";
	/**
	 * multi-purpose VO-DML role
	 */
	public static final String REAL_VALUE ="ivoa:RealQuantity.value";
	/**
	 * Instance of the parser providing the API to acces data
	 */
	private LiteMappingParser liteMappingParser;
	/**
	 * Extracted data: List of individual reports for each SpaeseCube
	 * {@link ZtfTimeSeriesReport} is an internal class
	 */
	private ZtfTimeSeriesReport ztfTimeSeriesReport = new ZtfTimeSeriesReport();

	/**
	 * Constructor: trigger data parsing
	 * @throws Exception
	 */
	ZtfExplorer() throws Exception{
		this.initParser();
	}
	
	/**
	 * Parse the file and build the mapping maps
	 * @throws Exception
	 */
	private void initParser() throws Exception {
		URL url = ZtfExplorer.class.getResource(VOTABLE_RESOURCE);
		String sampleName =url.getFile();
		liteMappingParser = new LiteMappingParser(sampleName);
	}

	/**
	 * Extract the source measurements
	 * @throws Exception
	 */
	public void exploreDataSet() throws Exception{	
		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:Source.identifier");
		this.ztfTimeSeriesReport.sourceId = dataSet.getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("meas:EquatorialPosition.ra");
		this.ztfTimeSeriesReport.ra = dataSet.getSubelementsByRole(REAL_VALUE).get(0).getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("meas:EquatorialPosition.dec");
		this.ztfTimeSeriesReport.dec = dataSet.getSubelementsByRole(REAL_VALUE).get(0).getStringValue();
	}
	
	/**
	 * Extract the magnitude filter
	 * @throws Exception
	 */
	public void exploreFilters() throws Exception{
		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:FilterUrl");
		this.ztfTimeSeriesReport.filterUrl = dataSet.getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:Filter.name");
		this.ztfTimeSeriesReport.filterName = dataSet.getStringValue();
	}
	
	/**
	 * Extract the time series
	 * @throws Exception
	 */
	public void exploreData() throws Exception{		
		MappingElement pointArray = this.liteMappingParser.getFirstNodeWithRole("nd_point:TimeSeries.points");
		this.ztfTimeSeriesReport.nbPoints = pointArray.getLength();
		this.ztfTimeSeriesReport.columnMapping = pointArray.getColumnRoles();
        for( int i=0 ; i<this.ztfTimeSeriesReport.nbPoints ; i++ ){
        	MappingElement row = pointArray.getContentElement(i);
        	this.ztfTimeSeriesReport.points.add(
        			new Point(
        					row.getSubelementsByRole(REAL_VALUE).get(0).getStringValue(),
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
		System.out.println("        id:" + this.ztfTimeSeriesReport.sourceId);
		System.out.println("  position:" + this.ztfTimeSeriesReport.ra + " " + this.ztfTimeSeriesReport.dec);
		System.out.println("== Filter ==");
		System.out.println("      name:" + this.ztfTimeSeriesReport.filterName);
		System.out.println("       url:" + this.ztfTimeSeriesReport.filterUrl);
		System.out.println("== Time Series ==");
		System.out.println("  nb points:" + this.ztfTimeSeriesReport.nbPoints);
		System.out.print("col mapping: ");
		for( Entry<Integer, String> entry : this.ztfTimeSeriesReport.columnMapping.entrySet()){
			System.out.print(entry.getKey() + "=>" + entry.getValue() + " ");
		}
		System.out.println("");
		for( Point point : this.ztfTimeSeriesReport.points){
			System.out.println(point);
		}
	}

	/**
	 * Inner classes storing extracted data
	 * @author laurentmichel
	 */
	class ZtfTimeSeriesReport{
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
		ZtfExplorer timeSeriesExample = new ZtfExplorer();
		timeSeriesExample.exploreDataSet();
		timeSeriesExample.exploreFilters();
		timeSeriesExample.exploreData();
		timeSeriesExample.printReport();
	}
	
	/*
	 * 
# Scale font and line width (dpi) by changing the size! It will always display stretched.
set terminal svg size 800,600 enhanced background rgb 'white' fname 'arial'  fsize 10 butt solid
set output 'out.svg'

# Key means label...
set key inside bottom right
set xlabel 'Time (HJD)'
set ylabel 'G Magnitude'
set title 'Model For Source Data: ZTF Demo (L. Michel)'
set yrange [15:20]
set xtics rotate font "Courier,12"
set ytics font "Courier,12"

set label 1 "Source oid = 686103400034440"
set label 1 at graph 0.04, 0.85 tc lt 3 font "Times-Roman,12"
set label 2 "{/:Bold Position} = 98.0025 9.87147"
set label 2 at graph 0.04, 0.78 tc lt 3 font "Times-Roman,12"
set label 3 '{/:Bold Filter} = G-Band ({/:Italic http://svo.url.filter/zdf/g})'
set label 3 at graph 0.04, 0.71 tc lt 3 font "Times-Roman,12"

plot  "data.txt"  using 1:2 notitle with lines smooth unique


	 */
}
