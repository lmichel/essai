/**
 * 
 */
package votable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cds.savot.model.TDSet;
import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import mapping.MappingFilter;
import mapping.nodes.Instance;
import mapping.types.Numerical;
import mapping.types.Textual;

/**
 * This class manages the transformation of row data read by  {@link TableModel}
 * in an instance of the class given by the model.
 * With this current implementation only one role can be given to a column.
 * The instance returned by the object are cloned from the template in order 
 * to make sure to not alter it
 * @author laurentmichel
 *
 */
public class RowDataSchema implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Instance used as template. All constant fields are set out of the class and 
	 * The fields referencing columns are set dynamically from read data.
	 */
	public final Instance refInstance;
	/**
	 * Map keys are the columns numbers (starting at 0) and values are the roles attached to those columns
	 */
	private final Map<Integer, String> columnRoles = new LinkedHashMap<>();
	/**
	 * Map keys are the columns numbers (starting at 0) and values are the template component {@linkplain Textual or 
	 * {@link Numerical} which must be set with the column values
	 */
	private final Map<Integer, Textual> columnMapping = new LinkedHashMap<>();
	/**
	 * Object extracting data from the VOTable
	 */
	private final transient  TableModel tableModel;
	/**
	 * Optional data filter: only rows having a given value to a given column are valid 
	 * The filtering is done by {@link TableModel} on behalf of this filter
	 */
	public final MappingFilter mappingFilter;

	/**
	 * @param refInstance
	 * @param tableModel
	 * @param mappingFilter can be null
	 * @throws Exception
	 */
	public RowDataSchema(Instance refInstance, TableModel tableModel, MappingFilter mappingFilter) throws Exception{
		this.refInstance = refInstance;
		this.tableModel = tableModel;
		this.mappingFilter = mappingFilter;
		this.setColumnMapping();
	}

	/**
	 * Fill the maps binding data columns with model components
	 * @throws Exception
	 */
	private void setColumnMapping() throws Exception {
		if( this.refInstance == null ){
			return;
		}
		List<String> keysOfTableColumns = this.refInstance.getKeysOfAtomicTypeByValue("@");
		for( String keyOfTableColumns: keysOfTableColumns){
			for( MappingElement me : this.refInstance.getSubelementsByRole(keyOfTableColumns)){
				String colRole = me.getStringValue();
				int colNumber = tableModel.getFieldIndexByIdOrName(colRole.substring(1));
				this.columnMapping.put(colNumber, (Textual)me);
				this.columnRoles.put(colNumber, keyOfTableColumns);
			}
		}
	}
	
	/**
	 * Return the number of the data columns having the role
	 * @param role
	 * @return
	 * @throws UnconsistantResourceException
	 */
	public int getFieldIndexByRole(String role) throws UnconsistantResourceException{
		for( Entry <Integer, String> entry: this.columnRoles.entrySet()){
			if( entry.getValue().equalsIgnoreCase(role)){
				return entry.getKey();
			}
		}
		throw new UnconsistantResourceException("No column with role "+ role + " found");
	}
	
	/**
	 * Returns a instance clone of the template and set with data read into the TDSet
	 * @param tdSet
	 * @return
	 * @throws Exception
	 */
	public Instance buildInstance(TDSet tdSet) throws Exception{
		for( Entry<Integer, Textual> entry: this.columnMapping.entrySet()){
			Textual textual = entry.getValue();
			textual.setValue(tdSet.getItemAt(entry.getKey()).getContent());
		}
		return Instance.deepClone(this.refInstance);		
	}

	/**
	 * @return the map binding column numbers with roles
	 */
	public Map<Integer, String> getColumnRoles(){
		return this.columnRoles;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String retour = "rowDataSchema [";
		for( Entry <Integer, String> entry: this.columnRoles.entrySet()){
			retour += "(" + entry.getKey() + " > " + entry.getValue() + ") ";
		}
		return retour + "]";
	}

}
