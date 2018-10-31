package mapping.nodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exceptions.MissingResourceException;
import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import mapping.MappingFilter;
import mapping.types.Array;
import mapping.types.Tuple;
import votable.RowDataSchema;
import votable.TableModel;

/**
 * This class handle the content of a ARRAY element
 * It does not contain data.
 * This is a facade implementing a {@link MappingElement} API to the {@link TableModel}
 * Its content is a single {@link Instance} used a template fo the generation of row instances
 * 
 * @author laurentmichel
 *
 */
public class DataTableCollection extends MappingNode
{
	/**
	 * just for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Instance used a template for the row object
	 */
	private Instance rowDataSchemaTemplate;
	/**
	 * Row object builder
	 */
	private RowDataSchema rowDataSchema;
	/**
	 * Data access
	 */
	private transient TableModel tableModel;
	/**
	 * Filter used to select row values when the content is a set of tuples
	 * not used yet
	 */
	private MappingFilter mappingFilter;

	/**
	 * @param vodmlId
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public DataTableCollection(final String vodmlId, final String role, final String name) throws UnconsistantResourceException {
		super(vodmlId, role, name);
	}
	/**
	 * @param vodmlId
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public DataTableCollection(final String vodmlId, final String role) throws UnconsistantResourceException {
		super(vodmlId, role);
	}

	/**
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public DataTableCollection(final String role) throws UnconsistantResourceException {
		super(role);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()  {
		try {
			final StringBuilder sb = new StringBuilder("COLLECTION " + this.getNodeId().toString());
			sb.append("\n");
			return sb.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByRole(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByRole(String role) throws Exception{
		if( this.rowDataSchemaTemplate != null ) {
			return this.rowDataSchemaTemplate.getSubelementsByRole(role);
		} else {
			return new ArrayList<>();
		}
	}


	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#setMappingFilter(java.lang.String, java.lang.String)
	 */
	@Override
	public void setMappingFilter(String key, String value) throws MissingResourceException {
		this.mappingFilter = new MappingFilter(key, value);
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#setMappingFilter(java.lang.String, java.lang.String)
	 */
	@Override
	public void setMappingFilter(MappingFilter mappingfFilter) throws MissingResourceException {
		this.mappingFilter = mappingfFilter;

	}
	
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement) throws Exception{
	}

	/**
	 * Remove the mapping filter. 
	 * The setValuesFromTableModel must be invoked after
	 */
	public void suppressMappingFilter() {
		this.mappingFilter = null;
	}

	/**
	 * Add a value to the content
	 * A column schema is automatically extracted from value if it is a {@link Tuple} or an {@link Instance}
	 * @param value
	 * @throws Exception when trying a column schema is already set
	 */
	public void addValue(Instance value) throws Exception{
		if( this.rowDataSchemaTemplate == null ){
			this.rowDataSchemaTemplate = value;
		} else{
			throw new UnconsistantResourceException("Cannot add a new element to a collection with a column schema.");
		}
	}

//	/* (non-Javadoc)
//	 * @see main.mapping.nodes.MappingNode#addMappingElement(java.lang.String, main.mapping.MappingElement)
//	 */
//	@Override
//	public void addMappingElement(final String key, final MappingElement mappingElement) throws Exception{
//		if( mappingElement instanceof Instance){
//			this.addValue((Instance) mappingElement);
//		} else {
//			throw new UnconsistantResourceException("Only INSTANCEs can be added to DataTableCollections");
//		}
//	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsWithRef()
	 */
	@Override
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		if( this.rowDataSchemaTemplate == null){
			return new ArrayList<>();
		} else {
			return this.rowDataSchemaTemplate.getSubelementsWithRef();
		}
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByClass(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<MappingElement> getSubelementsByClass(Class classe) throws Exception{
		if( this.rowDataSchemaTemplate != null ) {
			return this.rowDataSchemaTemplate.getSubelementsByClass(classe);
		} else {
			return new ArrayList<>();
		}
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByVodmlId(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByVodmlId(String vomdmlid) throws Exception{
		if( this.rowDataSchemaTemplate != null ) {
			return this.rowDataSchemaTemplate.getSubelementsByVodmlId(vomdmlid);
		} else {
			return new ArrayList<>();
		}
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#replaceElement(main.mapping.MappingElement, main.mapping.MappingElement)
	 */
	@Override
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		if( this.rowDataSchemaTemplate != null ) {
			this.rowDataSchemaTemplate.replaceElement(formerElement, newElement);
		} else {
			return ;
		}
	}



	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#getMappingElement(int)
	 */
	@Override
	public MappingElement getContentElement(int index) throws Exception{
		if( this.tableModel == null) {
			throw new MissingResourceException("Cannot get element while there is VOTable modell (" + this.nodeId + ")");
		}
		return this.tableModel.getRowInstanceAt(index, this.rowDataSchema);
	}

	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#getLength()
	 */
	@Override
	public int getLength() throws MissingResourceException {
		if( this.tableModel == null) {
			throw new MissingResourceException("Cannot get element while there is VOTable model (" + this.nodeId + ")");
		}
		return this.tableModel.getLength(this.rowDataSchema);
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getColumnRoles()
	 */
	@Override
	public Map<Integer, String> getColumnRoles() throws Exception{
		if( this.rowDataSchemaTemplate == null) {
			throw new MissingResourceException("Cannot get column roles because there is no data schema template (" + this.nodeId + ")");
		}
		return this.rowDataSchemaTemplate.getColumnRoles();
	}


	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#setValuesFromTableModel(main.votable.TableModel)
	 */
	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		this.tableModel = tableModel;
		this.rowDataSchema = new RowDataSchema(this.rowDataSchemaTemplate, this.tableModel, this.mappingFilter);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#groupContentByKeyValue(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Array> groupContentByColumnValue(String groupByKey) throws Exception{
		int keyIndex= this.rowDataSchema.getFieldIndexByRole(groupByKey);
		Map<String, Array> retour = new LinkedHashMap<>();
		
		for( int i=0 ; i<this.tableModel.getLength(this.rowDataSchema); i++){
			String keyValue = this.tableModel.getColumnValueAt(i, keyIndex, this.rowDataSchema);
			Array<Instance> array;
			if( (array = retour.get(keyValue)) == null){
				array = new Array<>();
				retour.put(keyValue, array);
			}
			array.addValue(this.tableModel.getRowInstanceAt(i, this.rowDataSchema));
		}
		return retour;
	}

}
