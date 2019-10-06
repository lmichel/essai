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
import parser.TemplateModel;

/**
 * This is an example of using the JAVA API to extract data from an annotated VOTable
 * The data extraction only relies on selectors based on dmroles
 * Developed in the frame of the CAB_MSD proposal
 * 
 * @author laurentmichel
 *
 */
public class IC4665Explorer {

	/**
	 * The file to parse (must be in the classpath)
	 */
	public static final String VOTABLE_RESOURCE = "/test/xml/PhotAndProperMotionsIC4666_annot.vot";
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
	 * {@link ParsingReport} is an internal class
	 */
	private ParsingReport parsingReport = new ParsingReport();

	/**
	 * Constructor: trigger data parsing
	 * @throws Exception
	 */
	IC4665Explorer() throws Exception{
		this.initParser();
	}
	
	/**
	 * Parse the file and build the mapping maps
	 * @throws Exception
	 */
	private void initParser() throws Exception {
		URL url = IC4665Explorer.class.getResource(VOTABLE_RESOURCE);
		String sampleName =url.getFile();
		liteMappingParser = new LiteMappingParser(sampleName);
	}

	/**
	 * Extract the source measurements
	 * @throws Exception
	 */
	public void exploreDataSet() throws Exception{	

		System.out.println(this.liteMappingParser.getRootElement());
		TemplateModel me = this.liteMappingParser.getRootElement();
		for( MappingElement mappingElemnt: me.getMappingElements() ) {
			System.out.println("cccc");
		}



//		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:Source.identifier");
//		this.parsingReport.sourceId = dataSet.getStringValue();
//		dataSet = this.liteMappingParser.getFirstNodeWithRole("meas:EquatorialPosition.ra");
//		this.parsingReport.ra = dataSet.getSubelementsByRole(REAL_VALUE).get(0).getStringValue();
//		dataSet = this.liteMappingParser.getFirstNodeWithRole("meas:EquatorialPosition.dec");
//		this.parsingReport.dec = dataSet.getSubelementsByRole(REAL_VALUE).get(0).getStringValue();
	}
	
	/**
	 * Extract the magnitude filter
	 * @throws Exception
	 */
	public void exploreFilters() throws Exception{
		MappingElement dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:FilterUrl");
		this.parsingReport.filterUrl = dataSet.getStringValue();
		dataSet = this.liteMappingParser.getFirstNodeWithRole("cab-msd:Filter.name");
		this.parsingReport.filterName = dataSet.getStringValue();
	}
	
	/**
	 * Extract the time series
	 * @throws Exception
	 */
	public void exploreData() throws Exception{		
		MappingElement pointArray = this.liteMappingParser.getFirstNodeWithRole("nd_point:TimeSeries.points");
		this.parsingReport.nbPoints = pointArray.getLength();
		this.parsingReport.columnMapping = pointArray.getColumnRoles();
        for( int i=0 ; i<this.parsingReport.nbPoints ; i++ ){
        	MappingElement row = pointArray.getContentElement(i);
        	this.parsingReport.points.add(
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
		System.out.println("        id:" + this.parsingReport.sourceId);
		System.out.println("  position:" + this.parsingReport.ra + " " + this.parsingReport.dec);
		System.out.println("== Filter ==");
		System.out.println("      name:" + this.parsingReport.filterName);
		System.out.println("       url:" + this.parsingReport.filterUrl);
		System.out.println("== Time Series ==");
		System.out.println("  nb points:" + this.parsingReport.nbPoints);
		System.out.print("col mapping: ");
		for( Entry<Integer, String> entry : this.parsingReport.columnMapping.entrySet()){
			System.out.print(entry.getKey() + "=>" + entry.getValue() + " ");
		}
		System.out.println("");
		for( Point point : this.parsingReport.points){
			System.out.println(point);
		}
	}

	/**
	 * Inner classes storing extracted data
	 * @author laurentmichel
	 */
	class ParsingReport{
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
		IC4665Explorer timeSeriesExample = new IC4665Explorer();
		timeSeriesExample.exploreDataSet();
//		timeSeriesExample.exploreFilters();
//		timeSeriesExample.exploreData();
//		timeSeriesExample.printReport();
	}
	
	/*
	 * 
	 * 
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

plot  "data.txt"  using 1:2  with  points ps 1  notitle 


# This file is called   force.dat
# Force-Deflection data for a beam and a bar
# ZTF    G-MAG 
2458205.0119003966	17.7203445
2458206.0120819123	17.6563911
2458207.0147988475	17.655283
2458207.0236419393	17.6439056
2458208.0070065474	17.6286964
2458209.010419121	17.4842987
2458210.0052784304	17.4736118
2458211.0110995797	17.6098099
2458211.9905191842	17.5284748
2458228.9847133518	17.5495567
2458229.9952864954	17.7526989
2458230.9935673983	17.6660023
2458231.9520889674	17.6226463
2458231.9860262498	17.5567513
2458232.9964956613	17.6036644
2458234.9638895891	17.5921917
2458234.9855229836	17.576004
2458235.93308345	17.588459
2458235.9622983369	17.5945015
2458236.9273367631	17.5677948
2458236.9980473258	17.5301056
2458237.9002809161	17.6344852
2458237.9852169035	17.5502853
2458244.9548395402	17.6173325
2458246.9697821056	17.59795
2458247.8924792097	17.619175
2458252.942274123	17.5904884
2458254.9371540984	17.6001549
2458255.9271914777	17.5762405
2458258.977271853	17.5581779
2458259.9043874438	17.5654106
2458261.9697644045	17.5282135
2458262.9435250442	17.7012825
2458263.946303233	17.5123692
2458266.9669503467	17.5620041
2458268.9808708159	17.5670357
2458271.9649108932	17.7883568
2458272.9021715745	17.6099949
2458273.9631767585	17.5814323
2458274.9031795603	17.6402664
2458275.9437078917	17.6640091
2458276.873917317	17.6627502
2458277.873088053	17.613142
2458278.91480644	17.6178169
2458279.8728626901	17.6064682
2458280.9108063616	17.6229286
2458282.8960506506	17.5920753
2458283.9376380043	17.603117
2458284.8955865731	17.6211338
2458285.9242551704	17.6495094
2458286.9244032018	17.6341705
2458287.9050356075	17.6074219
2458289.914749389	17.6041412
2458290.9405896235	17.6074429
2458291.9393793209	17.6229248
2458292.9397191359	17.6447105
2458293.9252659436	17.6416435
2458295.8814026983	17.4948463
2458296.9514174233	17.6046524
2458297.9610240776	17.5984154
2458298.9818222793	17.5682449
2458299.9451640155	17.5800686
2458300.9826506805	17.613924
2458301.82604479	17.6353531
2458302.9385318747	17.5220356
2458303.9086063416	17.6600189
2458304.9323850777	17.6480961
2458305.8337406642	17.658123
2458306.8441490559	17.8449383
2458307.8408872266	17.5914879
2458310.8787587308	17.4988594
2458311.8781317277	17.6207848
2458312.8662303849	17.5739555
2458313.862858302	17.5983543
2458314.8981201653	17.5296707
2458315.919375984	17.5924187
2458316.8837896646	17.5713997
2458317.8969992632	17.5931549
2458318.90597163	17.5885468
2458319.8762966678	17.6301422
2458320.9201401183	17.6411171
2458321.9060303546	17.6036873
2458322.9319778113	17.6630363
2458323.9067316712	17.6588116
2458324.9245403004	17.6623344
2458325.9135976746	17.568161
2458326.9334412441	17.6621857
2458328.9517592038	17.5784645
2458329.9193424224	17.6668968
2458330.7928273845	17.6100693
2458331.8026540582	17.6815796
2458332.7420051214	17.6604137
2458334.8088057926	17.6353607
2458336.7780560171	17.6116676
2458337.8740214822	17.6001148
2458338.8747779387	17.6312103
2458340.8532325621	17.5484028
2458343.8095756308	17.6347008
2458344.8452558126	17.6251545
2458345.8151141978	17.6019173
2458346.8232001518	17.6055508
2458348.8529796097	17.5872269
2458349.872705535	17.6250706
2458350.8433111333	17.637598
2458351.8079552935	17.5251312
2458352.8446106426	17.6313972
2458360.6723203948	17.5408401
2458361.7138161515	17.5341606
2458362.7286334257	17.5938835
2458363.6969466698	17.5468254
2458364.7359279185	17.6005554
2458365.7826740732	17.6297379
2458366.7347236625	17.655138
2458367.7554618637	17.7206459
2458368.758051814	17.6992702
2458369.7720176158	17.6937065
2458370.7493288796	17.6691666
2458371.7729569101	17.6570663
2458372.7613771134	17.6800194
2458373.7611618023	17.6987305
2458374.7547422252	17.6487255
2458375.7353826081	17.7231407
2458376.7352574496	17.697197
2458377.7360341512	17.6882019
2458378.806320814	17.7061672
2458379.671940315	17.6697521
2458380.6775757265	17.7418213
2458381.7341338038	17.7126083
2458382.6919227536	17.6905403
2458383.6935167494	17.575346
2458384.7519243457	17.6984463
2458386.6544774477	17.7092171
2458387.6737531088	17.6413879
2458389.6659968169	17.7040215
2458390.6548785893	17.7158413
2458425.6610484165	17.7827778
2458426.6640042602	17.7881279
2458427.6597730224	17.7219238
2458428.6535743773	17.6875916
2458429.6618656707	17.7034302
2458430.6387471273	17.6978989
2458431.5967872269	17.7322769
2458432.6522170468	17.6853714
2458433.6412508516	17.6007843
2458434.6358516887	17.673584
2458435.6300012548	17.6361961
2458469.6168436781	17.6808472

	 */
}
