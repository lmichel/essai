package parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mapping.GroupByProcessor;
import mapping.MappingElement;
import mapping.MappingModel;
import votable.VotableModel;

/**
 * Model for a template mapping
 * These objects are built by the parser {@link VodmlBlockParser}.
 * The are merged in a single {@link MappingModel} when data are spread out 
 * in different tables
 * The TemplateModel containing the mapping root (element with a drmrole==root or dmrole==modelname)
 * host a specific reference of the element
 * This class is not supposed to be visible for the end user
 * @author laurentmichel
 *
 */
public class TemplateModel {
	/**
	 * All lowest level elements
	 */
	private List<MappingElement> mappingElements = new ArrayList<>();
	/**
	 * Pointer the model root element (drmrole==root or dmrole==modelname)
	 * Can be null
	 */
	private MappingElement rootElement;
	/**
	 * Identifier of the table mapped by the template
	 */
	public final String tableId ;
	/**
	 * Used to group table rows by some criteria and to transform the 
	 * set of mappingElements in a collection of this set
	 */
	private GroupByProcessor groupByProcessor;

	/**
	 * @param tableId
	 * @param mappingElements
	 */
	public TemplateModel(String tableId, List<MappingElement> mappingElements){
		this.mappingElements = mappingElements;
		this.tableId = tableId;
	}

	/**
	 * @return
	 */
	public List<MappingElement> getMappingElements() {
		return this.mappingElements;
	}
	/**
	 * @param rootElement
	 */
	public void setRootElement(MappingElement rootElement){
		this.rootElement = rootElement;
	}
	/**
	 * @return
	 */
	public MappingElement getRootElement(){
		return this.rootElement;
	}
	/**
	 * @return
	 */
	public boolean hasRootElement(){
		return (this.rootElement != null);
	}
	/**
	 * @param groupByProcessor
	 */
	public void setGroupByProcessor(GroupByProcessor groupByProcessor){
		this.groupByProcessor = groupByProcessor;
	}
	/**
	 * @param votableModel
	 * @throws Exception
	 */
	public void setValuesFromTableModel(VotableModel votableModel) throws  Exception {
		for( MappingElement mappingElemnt: mappingElements ) {
			mappingElemnt.setValuesFromTableModel(votableModel.getTable(this.tableId));
		}
		if( this.rootElement != null){
			this.rootElement.setValuesFromTableModel(votableModel.getTable(this.tableId));
		}
		if( this.groupByProcessor != null && this.rootElement!= null){
			Map<String, MappingElement> groupedRoots = this.groupByProcessor.run();
			this.rootElement.emptyData();

			for( Entry<String, MappingElement> entry: groupedRoots.entrySet()){
				this.rootElement.addMappingElement(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * @return number of children
	 */
	public int getLength() {
		if( this.rootElement != null ){
			return 1;
		} else {
			return this.mappingElements.size();
		}
	}

	/**
	 * Returns of flat set of all sub-elements of the given role (vodml)
	 * @param role
	 * @return 
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<MappingElement> getElementsByRole(String role) throws Exception{
		List<MappingElement> retour = new ArrayList<>();

		for( MappingElement mappingElement: this.mappingElements){
			if( role.equalsIgnoreCase(mappingElement.getVodmlRole()) ) {
				retour .add(mappingElement);
			}
			retour.addAll(mappingElement.getSubelementsByRole(role));
		}
		return retour;
	}

	/**
	 * Returns of flat set of all sub-elements of the given role (vodml)
	 * @param role
	 * @return 
	 * @throws Exception when not applicable (or any other error)
	 */
	public List<MappingElement> getElementsById(String vodmlid) throws Exception{
		List<MappingElement> retour = new ArrayList<>();

		for( MappingElement mappingElement: this.mappingElements){
			if( mappingElement.hasVodmlId(vodmlid)  ) {
				retour .add(mappingElement);
			}
			retour.addAll(mappingElement.getSubelementsByVodmlId(vodmlid));
		}
		return retour;
	}

	/**
	 * Returns of flat set of all sub-elements having a reference to another element
	 * @param classe Java class
	 * @return
	 * @throws Exception 	
	 */
	public Set<MappingElement> getElementsWithRef() throws Exception{
		Set<MappingElement> retour = new HashSet<>();

		for( MappingElement mappingElement: this.mappingElements){
			if( mappingElement.getElementRef() != null ) {
				retour .add(mappingElement);
			}
			retour.addAll(mappingElement.getSubelementsWithRef());
		}
		return retour;
	}
	
	/**
	 * Replace the instance formerElement with newElement.
	 * This method is used to resolve cross references. It must not used from the public API
	 * @param formerElement
	 * @param newElement
	 * @throws Exception
	 */
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		for( MappingElement mappingElement: this.mappingElements){
			mappingElement.replaceElement(formerElement, newElement);
		}
	}


}