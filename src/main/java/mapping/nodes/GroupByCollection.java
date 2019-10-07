/**
 * 
 */
package mapping.nodes;

import java.util.ArrayList;
import java.util.List;

import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import votable.TableModel;

/**
 * This type of collection implemeng the SET element
 * .. work in porgress
 * @author laurentmichel
 *
 */
public class GroupByCollection extends MappingNode {
	/**
	 * Just for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * role of the column which values are used to group data
	 */
	public final String groupByKey;
	private Instance content;

	/**
	 * @param id
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public GroupByCollection(final String id, final String role, final String name, final String groupByKey) throws UnconsistantResourceException {
		super(id, role, name);
		this.groupByKey = groupByKey;
	}

	/**
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public GroupByCollection(final String role, final String groupByKey) throws UnconsistantResourceException {
		super(role);
		this.groupByKey = groupByKey;
	}
	
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement) throws Exception{
		if( this.content == null ){
			this.content = (Instance) mappingElement;
		} else {
			// This method is invoke for each table row
			//@@@@@throw new UnconsistantResourceException("A GroupBy collection can only hots one instance");
		}
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByClass(java.lang.Class)
	 */
	@Override
	public List<MappingElement> getSubelementsByClass(Class classe) throws Exception{
		if( this.content != null ) {
			return this.content.getSubelementsByClass(classe);
		} else {
			return new ArrayList<MappingElement>();
		}
	}


	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		this.content.setValuesFromTableModel(tableModel);
	}
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsWithref()
	 */
	@Override
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		return this.content.getSubelementsWithRef();
	}

}
