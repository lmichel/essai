/**
 * 
 */
package test.parser;

import cds.savot.model.FieldSet;
import cds.savot.model.ParamSet;
import cds.savot.model.ResourceSet;
import cds.savot.model.SavotData;
import cds.savot.model.SavotField;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTR;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotTableData;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.model.TRSet;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;

/**
 * @author michel
 *
 */
public class SavotWraper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String sampleName = SavotWraper.class.getResource("/test/xml/gaia_multicol.xml").getFile();;
		System.out.println(sampleName);
		SavotPullParser parser = new SavotPullParser(sampleName, SavotPullEngine.FULLREAD, false);
		//SavotTR savotTR = parser.getNextTR(); 
		SavotVOTable voTable = parser.getVOTable();
		
		System.out.println(voTable);
		voTable.getResources();
		System.out.println(voTable.getResources().getItems().size());
		ResourceSet resourcseSet = voTable.getResources();
        for( SavotResource savotResource: resourcseSet.getItems() ) {
        	System.out.println("Resource " + savotResource.getName());
        	for(  SavotTable table: savotResource.getTables().getItems()){
        		FieldSet fieldSet = table.getFields();
        		for( SavotField savotFiled: fieldSet.getItems()){
        			System.out.println(savotFiled.getName() + " " + savotFiled.getId());
        		}
        		ParamSet paramSet = table.getParams();
        		if( paramSet.getItemCount() > 0 ){
        		for( SavotParam savotParam: paramSet.getItems()){
        			System.out.println(savotParam.getName());
        		}
        		}
        		System.out.println("Table " +  table.getName() + " " + table.getId());
        		SavotData data = table.getData();
        		SavotTableData savotTableData = data.getTableData();
        		TRSet trSet = savotTableData.getTRs();
        		for( SavotTR savotTR: trSet.getItems()){
            		TDSet tdSet = savotTR.getTDs();
            		for( int i=0 ; i<tdSet.getItemCount() ; i++){
            			System.out.print(tdSet.getItemAt(i).getContent() + " " );
            		}
            		System.out.println("");
        		}
        	}
        }		
	}

}
