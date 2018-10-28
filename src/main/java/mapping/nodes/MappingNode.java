/**
 * 
 */
package mapping.nodes;

import java.util.List;
import java.util.Set;

import exceptions.MappingClassException;
import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import mapping.MappingElementRef;
import mapping.MappingFilter;
import votable.TableModel;
import votable.VotableModel;

/**
 * Super class for all mapping elements with {@link MappingNodeId}
 * The subclasses roughly match the VO-DML classes.
 * The constructors wrap the {@link MappingNodeId} constructors
 * @author michel
 *
 */
public abstract class MappingNode extends MappingElement {
	/**
	 * Node identifier
	 */
	public final MappingNodeId nodeId;
	
	
	/**
	 * @param vodmlId
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public MappingNode(final String vodmlId, final String role, final String name) throws UnconsistantResourceException {
		this.nodeId = new MappingNodeId(vodmlId, role, name);
	}
	
	/**
	 * @param vodmlId
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public MappingNode(final String vodmlId, final String role) throws UnconsistantResourceException {
		if( vodmlId != null){
			this.nodeId = new MappingNodeId(vodmlId, role);
		} else {
			this.nodeId = new MappingNodeId(role);
		}
	}
	
	/**
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public MappingNode(final String role) throws UnconsistantResourceException {
		this.nodeId = new MappingNodeId(role);
	}

	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement) throws Exception{
		throw new MappingClassException("No element can be added by key to an instance of " + this.getClass());
	}

	@Override
	public MappingElement getContentElement(final String key)  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return en Element value by key");
	}
	@Override
	public MappingElement getContentElement(final int index)  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return en Element by index");
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#setElementRef(java.lang.String, java.lang.String)
	 */
	@Override
	public void setElementRef(final String tableID, final String dmRole){
		this.nodeId.setElementRef(tableID, dmRole);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#setElementRef(java.lang.String)
	 */
	@Override
	public void setElementRef(final String elementRef){
		this.nodeId.setElementRef(elementRef);
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getElementRef()
	 */
	@Override
	public MappingElementRef getElementRef() throws MappingClassException {
		return this.nodeId.getElementRef();
	}

	@Override
	public int getLength()  throws Exception{
		return -1;
	}

	@Override
	public List<String> getKeySet()  throws Exception{
		return null;
	}

	@Override
	public boolean isLeaf() throws Exception {
		return false;
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getRole()
	 */
	@Override
	public String getVodmlRole() {
		return this.nodeId.role;
	}
	@Override
	public String getVodmlId() {
		return this.nodeId.vodmlId;
	}

	@Override
	public String getStringValue() throws Exception {
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return en String value");
	}

	@Override
	public double getNumericalValue()  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return en Numerical value");
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getChildByRole(java.lang.String)
	 */
	@Override
	public MappingElement getFirstChildByRole(final String role) throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not select child by rolee");
	}

	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not resolve value by reference");
	}

	@Override
	public MappingNodeId getNodeId() throws Exception{
		return this.nodeId;
	}

}
