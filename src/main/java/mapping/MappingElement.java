/**
 * 
 */
package mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import exceptions.MappingClassException;
import exceptions.UnconsistantResourceException;
import mapping.nodes.DataTableCollection;
import mapping.nodes.MappingNode;
import mapping.nodes.MappingNodeId;
import mapping.types.Array;
import votable.TableModel;

/**
 * Super class of all entities used to model mapping blocks
 * Some of the methods can be applied to instance of any sublclass and some
 * others only a to specific subclasses. 
 * When in method is invoked on a subclass not supporting the service, an exception is risen
 * add
 * The mapping elements has 2 branches:
 * - The {@link MappingNode} which have an DM identifiers
 * - The {@link mapping.types.Type} which are containers for simple value(s)
 * 
 * All Exception are thrown in order to delegate the processing odf any sort 
 * of error to the upper level software
 * 
 * @author michel
 *
 */
public abstract class MappingElement implements Serializable{
	/**
	 * requested for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * just to use always the same message
	 */
	public  String NO_INVOKE = "Method must not be invoked from root level (" + this.getClass() + ")";
	
	/**
	 * return the value attached to the key. 
	 * Only applicable for MappingElment based on a key/value mechanism
	 * @param key
	 * @return the mapping element matching the containing the value
	 * @throws Exception when not applicable (or any other error)
	 */
	public MappingElement getContentElement(String key) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * return the value located at the index position. 
	 * Only applicable for MappingElment based on a array schema
	 * @param index
	 * @return the mapping element matching the containing the value
	 * @throws Exception when not applicable (or any other error)
	 */
	public MappingElement getContentElement(int index) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * Set a reference to an element by using a tabeid/role pair
	 * @param tableID
	 * @param dmRole
	 * @throws MappingClassException 
	 */
	public void setElementRef(final String tableID, final String dmRole) throws MappingClassException{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Set a reference to an element by using a element ID
	 * @param elementRef
	 * @throws MappingClassException 
	 */
	public void setElementRef(final String elementRef) throws MappingClassException{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * @return The reference to another mapping element if is exists or null
	 * @throws MappingClassException
	 */
	public MappingElementRef getElementRef() throws MappingClassException{
		throw new MappingClassException(NO_INVOKE);
	}

	/**
	 * Remove the instance data when relevant
	 * @throws Exception
	 */
	public void emptyData() throws Exception {}

	/**
	 * Attach the mappingELement instance the key
	 * Only applicable for MappingElment based on a key/value mechanism
	 * @param key
	 * @param mappingElement
	 * @throws Exception when not applicable (or any other error)
	 */
	public void addMappingElement(String key, MappingElement mappingElement) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}

	/**
	 * Only applicable for MappingElment based on a array schema
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public int getLength() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Only applicable for MappingElment based on a key/value mechanism
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<String> getKeySet() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Check if the element is a leag of the mapping hierarchy.
	 * The leaf must be a subclass of {@link mapping.types.Type}
	 * @return True if it is a leaf
	 * @throws Exception when not applicable (or any other error)
	 */
	public boolean isLeaf() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * @return the role of the element or null
	 */
	public String getVodmlRole() {
		return null;
	}
	/**
	 * @return the Id of the element or null
	 */
	public String getVodmlId() {
		return null;
	}
	/**
	 * @param vodmlid
	 * @return true if the instance id matches id
	 */
	public boolean hasVodmlId(final String vodmlid){
		return vodmlid.equalsIgnoreCase(this.getVodmlId());
	}

	/**
	 * Returns the value of the element (if make sense) as a String
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public String getStringValue() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Returns the value of the element as a double (if make sense)
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public double getNumericalValue() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Return the first child element having the the given role (vodlml)
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public MappingElement getFirstChildByRole(String role) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Returns of flat set of all sub-elements of the given role (vodml)
	 * @param role
	 * @return 
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<MappingElement> getSubelementsByRole(String role) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * Return the first subelement with role. 
	 * If there are more than one or none, a UnconsistantResourceException is risen
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public MappingElement getOneSubelementByRole(String role) throws Exception{
		List<MappingElement> retour = this.getSubelementsByRole(role);
		int size;
		if( (size = retour.size()) > 1 ){
			throw new UnconsistantResourceException("There are more than 1 element (" + retour.size() + ") with role=" + role + " cannot get one");
		} else if( size == 0 ){
			throw new UnconsistantResourceException("There are no element with role=" + role + " cannot get one");
		} else {
			return retour.get(0);
		}
	}
	/**
	 * Returns of flat set of all sub-elements of the given class (java)
	 * @param classe Java class
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<MappingElement> getSubelementsByClass(Class classe) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * Returns of flat set of all sub-elements having a reference to another element
	 * @return
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * Returns of flat set of all sub-elements of the given vodmlid
	 * @param vomdmlid
	 * @return 
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<MappingElement> getSubelementsByVodmlId(String vomdmlid) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}

	/**
	 * Return the keys (usually the roles) of all Type instances (Textual in fact) 
	 * having a value starting with startWith
	 * @param startWith
	 * @return
	 * @throws Exception
	 */
	public List<String> getKeysOfAtomicTypeByValue(String startWith) throws Exception {
		return new ArrayList<>();
	}

	/**
	 * Set the value of the Type instance located with key with value
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setAtomicTypeValue(String key, String value) throws Exception {
	}

	/**
	 * @return the node ID of the instance 
	 * @throws Exception when not applicable (or any other error)
	 */
	public MappingNodeId getNodeId() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Set recursively all values of the current MappingElemenet and its children. 
	 * Values are taken out from the {@link TableModel} 
	 * by using the references stored int the {@link MappingNodeId}
	 * @param tableModel object implementing a API to access VOTable data by using references and roles
	 * @throws Exception when not applicable (or any other error)
	 */
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * Replace the instance formerElement with newElement.
	 * This method is used to resolve cross references. It must not used from the public API
	 * @param formerElement
	 * @param newElement
	 * @throws Exception
	 */
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		return;
	}
	
	
	/*******************************
	 * The following methods are dedicated to the Collection processing.
	 * @throws MappingClassException 
	 */
	
	/** 
	 * Return a map where keys are the column numbers and vakues are the roles attached to these column.
	 * The current implementation does not support multiple roles for a single column
	 * @return
	 * @throws MappingClassException
	 */
	public Map<Integer, String> getColumnRoles() throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * To be applied on {@link DataTableCollection} or {@link mapping.types.Array}
	 * @param groupByKey dmrole of the column used by the group by
	 * @return a map with the groupByKey columns values as keys
	 * @throws Exception when not applicable (or any other error)
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Array> groupContentByColumnValue(String groupByKey) throws Exception{
		throw new MappingClassException(NO_INVOKE);
	}
	
	/**
	 * Set a data filter. 
	 * The filter must be set befor setting he values (setValuesFromTableModel)
	 * @param mappingFilter
	 * @throws Exception
	 */
	public void setMappingFilter(MappingFilter mappingFilter)  throws Exception  {
		throw new MappingClassException(NO_INVOKE);
	}
	/**
	 * Set a data filter. 
	 * The filter must be set befor setting he values (setValuesFromTableModel)
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setMappingFilter(String key, String value)  throws Exception  {
		throw new MappingClassException(NO_INVOKE);
	}

}
