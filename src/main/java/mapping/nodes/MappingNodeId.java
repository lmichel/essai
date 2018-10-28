/**
 * 
 */
package mapping.nodes;

import java.io.Serializable;

import exceptions.MappingClassException;
import exceptions.UnconsistantResourceException;
import mapping.MappingElementRef;

/**
 * Model of a mapping element identification
 * @author michel
 *
 */
public class MappingNodeId implements Serializable{
	/**
	 * requested for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * default value for element identifiers
	 */
	public static final String NOID = "no-id";
	/**
	 * default value for element names
	 */
	public static final String NONAME = "no-name";
	/**
	 * Element identifier
	 */
	public final String vodmlId;
	/**
	 * VO-DML role: This attribute must be set in any case
	 */
	public final  String role;
	/**
	 * Element name
	 */
	public final  String name;
	/**
	 * Model of a reference possibly attached to then the current element
	 */
	private MappingElementRef mappingElementRef;

	/**
	 * @param id element identifier
	 * @param role VO-DML role
	 * @param name Element name 
	 * @throws UnconsistantResourceException 
	 */
	public MappingNodeId(final String vodmlId, final String role, final String name) throws UnconsistantResourceException {
		super();
		this.vodmlId = vodmlId;
		this.role = role;
		this.name = name;
		if( this.role == null ){
			throw new UnconsistantResourceException(this.toString() + " has no role");
		}
	}
	
	/**
	 * Name is set with default values
	 * @param id element identifier
	 * @param role VO-DML role
	 * @throws UnconsistantResourceException 
	 */
	public MappingNodeId(final String vodmlId, final String role) throws UnconsistantResourceException {
		super();
		this.vodmlId = vodmlId;
		this.role = role;
		this.name = NONAME;
		if( this.role == null ){
			throw new UnconsistantResourceException(this.toString() + " has no role");
		}
	}
	/**
	 * ID and name are set with default values
	 * @param role VO-DML role
	 * @throws UnconsistantResourceException 
	 */
	public MappingNodeId(final String role) throws UnconsistantResourceException {
		super();
		this.vodmlId = NOID;
		this.role = role;
		this.name = NONAME;
		if( this.role == null ){
			throw new UnconsistantResourceException(this.toString() + " has no role");
		}
	}
	
	@Override
	public String toString() {
		return "id=" + this.vodmlId + " role=" + this.role 
				+ ((this.mappingElementRef != null)? (" ref=[" + this.mappingElementRef + "]"):"");
	}
	
	/**
	 * @param role
	 * @return true if the instance id matches role
	 */
	public boolean hasRole(final String role){
		return this.role.equalsIgnoreCase(role);
	}
	/**
	 * @param role
	 * @return true if the instance id matches id
	 */
	public boolean hasVodmlId(final String id){
		return this.vodmlId.equalsIgnoreCase(id);
	}
	
	/**
	 * Set a reference to an element by using a tabeid/role pair
	 * @param tableID
	 * @param dmRole
	 */
	public void setElementRef(final String tableID, final String dmRole){
		this.mappingElementRef = new MappingElementRef(tableID, dmRole);
	}
	/**
	 * Set a reference to an element by using a element ID
	 * @param elementRef
	 */
	public void setElementRef(final String elementRef){
		this.mappingElementRef = new MappingElementRef(elementRef);
	}
	
	/**
	 * @return The reference to another mapping element if is exists or null
	 * @throws MappingClassException
	 */
	public MappingElementRef getElementRef() throws MappingClassException{
		return this.mappingElementRef;
	}

}
