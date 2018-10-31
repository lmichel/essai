/**
 * 
 */
package votable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cds.savot.model.FieldSet;
import cds.savot.model.ParamSet;
import cds.savot.model.SavotField;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotTR;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotTableData;
import cds.savot.model.TDSet;
import cds.savot.model.TRSet;
import exceptions.UnconsistantResourceException;
import mapping.MappingFilter;
import mapping.nodes.Instance;
import mapping.types.Textual;

/**
 * API over SAvot making easier the random acces to any data 
 * in a given TABLE of a VOTable.
 * TableModel is an in-memory processor
 * 
 * @author michel
 *
 */
public class TableModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * TABLE name
	 */
	public final String name;
	/**
	 * TABLE identifier
	 */
	public final String id;
	/**
	 * FIELD list modeled by Savot
	 */
	private  List<SavotField> fields = new ArrayList<>();
	/**
	 * FIELD list modeled by Savot
	 */
	private  List<SavotParam> params = new ArrayList<>();
	/**
	 * Set if TR elements modeled by Savot
	 */
	private TRSet dataRows;

	/**
	 * @param table
	 */
	public TableModel(SavotTable table){
		this.name = (table.getName() == null)? "no-name": table.getName();
		this.id = (table.getId() == null)? "no-id": table.getId();
		this.setFields(table);
		this.setParams(table);
		this.setData(table);
	}

	/**
	 * Extract the FIELD from the TABLE
	 * @param table
	 */
	private void setFields(final SavotTable table) {
		FieldSet fieldSet = table.getFields();
		if( fieldSet.getItemCount() > 0 ){
			for( SavotField savotFiled: fieldSet.getItems()){
				this.fields.add(savotFiled);
			}
		}
	}

	/**
	 * Extract the PARAM from the TABLE
	 * @param table
	 */
	private void setParams(final SavotTable table) {
		ParamSet paramSet = table.getParams();
		if( paramSet.getItemCount() > 0 ){
			for( SavotParam savotParam: paramSet.getItems()){
				this.params.add(savotParam);
			}
		}
	}

	/**
	 * Extract the TR set from the TABLE
	 * @param table
	 */
	private void setData(final SavotTable table){
		if( table.getData() != null && table.getData().getTableData() != null ) {
			SavotTableData savotTableData = table.getData().getTableData();
			this.dataRows = savotTableData.getTRs();
		} else  {
			this.dataRows = new TRSet();
		}
	}

	/**
	 * @return The number of rows matching rowDataSchema 
	 *
	public int getLength() {
		return this.tableLength;
	}*/

	/**
	 * Return the Savot FIELD selected by its rank (column number)
	 * @param index
	 * @return
	 */
	public SavotField getField(final int index){
		return this.fields.get(index);
	}

	/**
	 * Return the Savot PARAM selected by its rank (not really usefull)
	 * @param index
	 * @return
	 */
	public SavotParam getParam(final int index){
		return this.params.get(index);
	}

	/**
	 * Return the Savot FIELD index (colmun number) by its name
	 * @param name
	 * @return
	 */
	public int getFieldIndexByName(final String name){
		int retour = -1;
		for( int i=0 ; i<this.fields.size() ; i++ ){
			if( name.equalsIgnoreCase(this.fields.get(i).getName()) ) {
				retour = i;
				break;
			}
		}
		return retour;
	}

	/**
	 * Return the Savot FIELD index (colmun number) by its ID
	 * @param id
	 * @return
	 */
	public int getFieldIndexById(final String id){
		int retour = -1;
		for( int i=0 ; i<this.fields.size() ; i++ ){
			if( id.equalsIgnoreCase(this.fields.get(i).getId()) ) {
				retour = i;
				break;
			}
		}
		return retour;
	}

	/**
	 * Return the Savot FIELD index (colmun number) by its ID or its name if no ID matches
	 * @param id
	 * @return
	 */
	public int getFieldIndexByIdOrName(final String id){
		int retour = this.getFieldIndexById(id);
		if( retour == -1 ){
			retour = this.getFieldIndexByName(id);
		} 
		return retour;
	}

	/**
	 * Return the Savot FIELD by its ID or its name if no ID matches
	 * @param id
	 * @return
	 */
	public SavotField getFieldByIdOrName(final String id){
		int retour = this.getFieldIndexById(id);
		if( retour == -1 ){
			retour = this.getFieldIndexByName(id);
		} 
		return (retour  != -1)? this.getField(retour): null;
	}

	/**
	 * Return the Savot PARAM index by its name
	 * @param name
	 * @return
	 */
	public int getParamIndexByName(final String name){
		int retour = -1;
		for( int i=0 ; i<this.params.size() ; i++ ){
			if( name.equalsIgnoreCase(this.params.get(i).getName()) ) {
				retour = i;
				break;
			}
		}
		return retour;
	}

	/**
	 * Return the Savot PARAM index by its ID
	 * @param id
	 * @return
	 */
	public int getParamIndexById(final String id){
		int retour = -1;
		for( int i=0 ; i<this.params.size() ; i++ ){
			if( id.equalsIgnoreCase(this.params.get(i).getId()) ) {
				retour = i;
				break;
			}
		}
		return retour;
	}

	/**
	 * Return the Savot PARAM index by its ID or its name if no ID matches
	 * @param id
	 * @return
	 */
	public int getParamIndexByIdOrName(final String id){
		int retour = this.getParamIndexById(id);
		if( retour == -1 ){
			retour = this.getParamIndexByName(id);
		} 
		return retour;
	}

	/**
	 * Return the Savot PARAM by its ID or its name if no ID matches
	 * @param id
	 * @return
	 */
	public SavotParam getParamByIdOrName(final String id){
		int retour = this.getParamIndexById(id);
		if( retour == -1 ){
			retour = this.getParamIndexByName(id);
		} 
		return (retour  != -1)? this.getParam(retour): null;
	}
	/**
	 * Return the PARAM value by its ID or its name if no ID matches
	 * @param id
	 * @return
	 */
	public String getParamValueByIdOrName(final String id){
		SavotParam savotParam = this.getParamByIdOrName(id);
		if( savotParam != null ){
			return savotParam.getValue();
		} else {
			return null;
		}
	}

	private boolean isTdsetValid(TDSet tdSet, RowDataSchema rowDataSchema){
		if( rowDataSchema == null || rowDataSchema.mappingFilter == null ){
			return true;
		} else  {
			for( int i=0 ; i<tdSet.getItemCount() ; i++){
				if( this.fields.get(i).getName().equals(rowDataSchema.mappingFilter.key) 
						&& tdSet.getItemAt(i).getContent().equalsIgnoreCase(rowDataSchema.mappingFilter.value)){
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Compute the number of rows matching the schema
	 */
	public int getLength(RowDataSchema rowDataSchema){
		int lgth =0;
		for( SavotTR savotTR: this.dataRows.getItems()){
			if( this.isTdsetValid(savotTR.getTDs(), rowDataSchema) ){
				lgth++;
			}
		}
		return  lgth;
	}

	public TDSet getTdsetAt(int index, RowDataSchema rowDataSchema){
		int lgth = this.getLength(rowDataSchema);
		if( index <0 || index >= lgth){
			throw new ArrayIndexOutOfBoundsException("index " + index + " out if bounds (" + lgth + ")");
		}
		int cpt =0;
		for( SavotTR savotTR: this.dataRows.getItems()){
			TDSet tdSet = savotTR.getTDs();
			if( this.isTdsetValid(tdSet, rowDataSchema) ){
				if( cpt == index){
					return tdSet;
				}
				cpt++;
			}
		}
		throw new ArrayIndexOutOfBoundsException("Unable to find row #" + index);
	}

	public Instance getRowInstanceAt(int index, RowDataSchema rowDataSchema) throws Exception{
		int lgth = this.getLength(rowDataSchema);
		if( index <0 || index >= lgth){
			throw new ArrayIndexOutOfBoundsException("index " + index + " out of bounds (length=" + lgth + ")");
		}
		int cpt =0;
		for( SavotTR savotTR: this.dataRows.getItems()){
			TDSet tdSet = savotTR.getTDs();
			if( this.isTdsetValid(tdSet, rowDataSchema) ){
				if( cpt == index){
					if( rowDataSchema == null || rowDataSchema.refInstance == null){
						return getDefaultInstanceAt(tdSet);
					} else {
						return rowDataSchema.buildInstance(tdSet);
					}
				}
				cpt++;
			}
		}
		throw new ArrayIndexOutOfBoundsException("Unable to find row #" + index);
	}

	/**
	 * Returns an {@link Instance} where key are the fields names and values are those of tdSet
	 * @param tdSet
	 * @return
	 * @throws UnconsistantResourceException
	 */
	private Instance getDefaultInstanceAt(TDSet tdSet) throws UnconsistantResourceException{
		Instance instance = new Instance("default.row.instance");
		for( int i=0 ; i<tdSet.getItemCount() ; i++){
			instance.addMappingElement(this.fields.get(i).getName()
					, new Textual(tdSet.getItemAt(i).getContent()));
		}
		return instance;
	}


	public String getColumnValueAt(final int pos, final int colNumber, RowDataSchema rowDataSchema) {
		final TDSet tdSet = this.getTdsetAt(pos, rowDataSchema);
		return tdSet.getItemAt(colNumber).getContent();
	}

}
