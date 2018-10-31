package votable;

import java.util.LinkedHashMap;
import java.util.Map;

import cds.savot.model.ResourceSet;
import cds.savot.model.SavotField;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.pull.SavotPullEngine;
import cds.savot.pull.SavotPullParser;
import exceptions.MappingException;
import exceptions.MissingResourceException;
import exceptions.UnconsistantResourceException;
import mapping.nodes.Instance;

/**
 * API over SAvot making easier the random acces to any data 
 * of any TABLE in a VOTable.
 * TableModel is an in-memory processor
 * 
 * @author michel
 *
 */public class VotableModel {
	/**
	 * Map of the parsed TABLE
	 * ID or names are used as key.
	 */
	private final Map<String, TableModel> tables = new LinkedHashMap<>();
	
	/**
	 * Constructor.
	 * The VOtable parsing is done inside this constructor. 
	 * The VOTable content is made accessible once the instance is available
	 * @param votablePath
	 * @throws MappingException
	 */
	public VotableModel(final String votablePath) throws MappingException{
		SavotPullParser parser = new SavotPullParser(votablePath, SavotPullEngine.FULLREAD, false);
		SavotVOTable voTable = parser.getVOTable();
		voTable.getResources();
		ResourceSet resourcseSet = voTable.getResources();      	
		for( SavotResource savotResource: resourcseSet.getItems() ) {
			for(  SavotTable table: savotResource.getTables().getItems()){
				String ref;
				if( table.getId() != null ) {
					ref = table.getId();
				} else if( table.getName() != null)  {
					ref = table.getName();
				} else {
					throw new UnconsistantResourceException("Tabel without name nor id" );
				}
				if( this.tables.get(ref) != null ){
					throw new UnconsistantResourceException("More than one table named " + ref);
				}
				TableModel model = new TableModel(table);
				this.tables.put(ref, model);;
			}
		}

	}
	
	/**
	 * Return the {@link TableModel} of the table idtable
	 * @param idtable
	 * @return
	 * @throws MappingException
	 */
	public TableModel getTable(final String idtable) throws MappingException{
		TableModel retour = this.tables.get(idtable);
		if( retour == null ){
			throw new MissingResourceException("No table named " + idtable);
		}
		return retour;
	}

	/**
	 * Return the Savot FIELD of the table idtable selected by its rank
	 * @param idtable
	 * @param index
	 * @return
	 * @throws MappingException
	 */
	public SavotField getField(final String idtable, final int index) throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getField(index);
	}

	/**
	 * Return the Savot PARAM of the table idtable selected by its rank (not really usefull)
	 * @param idtable
	 * @param index
	 * @return
	 * @throws MappingException
	 */
	public SavotParam getParam(final String idtable, final int index) throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getParam(index);
	}

	/**
	 * Return the index of the Savot FIELD of the table tableid  by its name 
	 * @param idtable
	 * @param name
	 * @return
	 * @throws MappingException
	 */
	public int getFieldIndexByName(final String idtable, final String name) throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getFieldIndexByName(name);
	}
	

	/**
	 * Return the index of the Savot FIELD of the table tableid  by its ID 
	 * @param idtable
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public int getFieldIndexById(final String idtable, final String id) throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getFieldIndexById(id);
	}
	
	/**
	 * Return the index of the Savot FIELD of the table tableid  by its ID or its name if no ID matches
	 * @param idtable
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public int getFieldIndexByIdOrName(final String idtable, final String id) throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getFieldIndexByIdOrName(id);
	}
	
	/**
	 * Return the Savot FIELD of the table tableid  by its ID or its name if no ID matches
	 * @param idtable
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public SavotField getFieldByIdOrName(final String idtable, final String id)throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getFieldByIdOrName(id);
	}
	
	/**
	 * Return the index of the  Savot PARAM of table idtable by its name
	 * @param idtable
	 * @param name
	 * @return
	 * @throws MappingException
	 */
	public int getParamIndexByName(final String idtable, final String name)throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getParamIndexByName(name);
	}
	

	/**
	 * Return the index of the  Savot PARAM of table idtable by its ID
	 * @param idtable
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public int getParamIndexById(final String idtable, final String id)throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getParamIndexById(id);
	}
	
	/**
	 * Return the index of the  Savot PARAM of table idtable by its ID or its name if no ID matches
	 * @param idtable
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public int getParamIndexByIdOrName(final String idtable, final String id)throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getParamIndexByIdOrName(id);
	}
	
	/**
	 * The return {@link SavotParam} matching the PARAM of the table tableid having id as id or name
	 * returns null otherwise
	 * @param idtable of of the table to read
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public SavotParam getParamByIdOrName(final String idtable, final String id)throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getParamByIdOrName(id);
	}


	/**
	 * The return the value of the PARAM of the table tableid having id as id or name
	 * returns null otherwise
	 * @param idtable of of the table to read
	 * @param id
	 * @return
	 * @throws MappingException
	 */
	public String getParamValueByIdOrName(final String idtable, final String id)throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getParamValueByIdOrName(id);
	}

	/**
	 * Return the Savot {@link TDSet} corresponding the row index
	 * @param idtable of of the table to read
	 * @param index valid row number, different from the actual row number when a filter is applied
	 * @return
	 * @throws MappingException
	 */
	public TDSet getTdsetAt(final String idtable, final int index) throws MappingException{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getTdsetAt(index, null);
	}

	/**
	 * return a instance of {@link RowDataSchema#refInstance} set with the constant values of the refInstance
	 * and with the values of the row index
	 * @param idtable of of the table to read
	 * @param index valid row number, different from the actual row number when a filter is applied
	 * @return
	 * @throws Exception
	 */
	public Instance getRowInstanceAt(final String idtable, final int index) throws Exception{
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getRowInstanceAt(index, null);
	}

	/**
	 * Return one table cell element
	 * @param idtable of of the table to read
	 * @param index valid row number, different from the actual row number when a filter is applied
	 * @param colNumber
	 * @return
	 * @throws MappingException
	 */
	public String getColumnValueAt(final String idtable, final int index, final int colNumber) throws MappingException {
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getColumnValueAt(index, colNumber, null);
	}
	/**
	 * return the number of valid rows.
	 * This nulber can be lower than the actual number of rows when a filter is applied
	 * @param idtable
	 * @return
	 * @throws MappingException
	 */
	public int getLength(final String idtable) throws MappingException {
		TableModel tableModel = this.getTable(idtable);
		return tableModel.getLength(null);
	}

}
