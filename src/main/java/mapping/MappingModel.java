/**
 * 
 */
package mapping;

import java.util.Collection;
import java.util.List;

import mapping.nodes.MappingNode;

/** 
 * Model for a whole mapping block.
 * This object is presented to the client code to access model instance quantities
 * The model instance quantities can be reach by ussing selector based on VO-DML roles:
 * 
 * One can get one element by giving a list of roles, each one being a path element in the model
 * <code>
 * MappingElement sel = root.getNode("role1", "role2", "role3");
 * </code>
 
 * One can get a set of elements by giving one role a list of roles, each one being a path element in the model.
 * <code>
 * Set[MappingElement] sel = root.getElementsByRole("role1");
 * </code>
 * In this case the MappingElements are returned in a flat set independently from their original context.
 * This is the most convenient way to get model nodes when they are unique or to get in one shot all similar
 * model nodes (e.g. authors)
 * TODO remove this class and push getNode(String... roles ) in MappingElement
 * 
 * @author michel
 *
 */
public class MappingModel {
	/**
	 * Root element of mapping. Usually an instance of the model or a {@link Collection} of model instances
	 */
	public final MappingNode rootNode;

	/**
	 * @param rootNode
	 */
	public MappingModel(MappingNode rootNode) {
		super();
		this.rootNode = rootNode;
	}

	/**
	 * Look for the mapping element pointed by the roles
	 * @param roles array of roles denoting the path in the model leading to the wanted node
	 * @return The selected MappingElement or null
	 * @throws Exception 
	 */
	public MappingElement getNode(String... roles ) throws Exception{
		MappingElement retour = null;
		if( this.rootNode.getNodeId().role.equalsIgnoreCase(roles[0])) {
			MappingElement parent = this.rootNode;
			for( int i=1 ; i<roles.length ; i++){
				retour = parent.getFirstChildByRole(roles[i]);
				if( retour == null || retour.isLeaf() ){
					retour = null;
					break;
				} else {
					parent = retour;
				}
			}
		} 
		return retour;
	}	
	
	/**`Look for all elements having the requested role
	 * @param role
	 * @return all elements having the given role
	 * @throws Exception 
	 */
	public List<MappingElement> getElementsByRole(String role) throws Exception{
		return this.rootNode.getSubelementsByRole(role);
	}		
}
