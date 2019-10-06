/**
 * 
 */
package parser;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.MappingException;
import exceptions.MissingResourceException;
import exceptions.UnconsistantResourceException;
import exceptions.UnsupportedFeatureException;

import java.util.Set;

import mapping.MappingElement;
import mapping.MappingElementRef;
import mapping.nodes.Instance;
import votable.VotableModel;

/**
 * Top level class. It must be used by the end user
 * <code>
MappingLiteParser mappingLiteParser = new MappingLiteParser("myVoTable");

if( mappingLiteParser.implements("SimeSeries) {
    MappingElement collection = mappingLiteParser.getFirstChildrenByRole("dm:Photometry.Points");
    for( int i=0 ; i$lt;collection.getLength() ; i++){
	    MappingElement point = collection.getValue(i);
	    double time = point.getValue("dm:Photometry.Point.Time");
	    double magnitude = point.getValue("dm:Photometry.Point.Mag");
	    System.out.println("time = + time + " mag=" + mag);
	}
}
 * </code>
 * @author laurentmichel
 *
 */
public class LiteMappingParser {
	/**
	 * Absolute path of the VOTable to process
	 */
	private final String votableName;
	/**
	 * Object providing azn access to the VOTable data
	 */
	private  VotableModel votableModel;
	/**
	 * Map of the parsed TEMPLATES of the VOTable. Keys are the TEMPLATES ids which must match the TABLE ids.
	 * The TemplateModel corresponding to GLOBALS has the key "globals"
	 */
	private  Map<String, TemplateModel> templates;
	/**
	 * Reference to the template containing the dmrole=root element
	 */
	private  TemplateModel rootTemplate  =  null;

	/**
	 * Constructor.
	 * The VOtable parsing is done inside this constructor. 
	 * The VOTable content is made accessible once the instance is available
	 * @param voTable
	 * @throws MappingException
	 */
	public LiteMappingParser(String voTable) throws Exception{	
		this.votableName = voTable;
		this.setVotableModel();
		this.buildTemplates();
		this.setRootTemplate();
		this.resolveReferences();
	}

	/**
	 * Build the VOTable model
	 * @throws MappingException
	 */
	private void setVotableModel() throws MappingException {
		this.votableModel = new VotableModel(this.votableName);
	}
	
	/**
	 * Parses tne VOMDL block and stores the parsed templates
	 * @throws Exception
	 */
	private void buildTemplates() throws Exception{
		VodmlBlockParser vodmlBlockParser = new VodmlBlockParser(this.votableName);
		vodmlBlockParser.parse();

		this.templates = vodmlBlockParser.templates;
		for( Entry<String, TemplateModel> entry: this.templates.entrySet()){
			TemplateModel templateModel = entry.getValue();
			if( !entry.getKey().equalsIgnoreCase("globals")) {
				templateModel.setValuesFromTableModel(this.votableModel);
			}
		}
	}
	
	/**
	 * Extract the reference of the root template
	 * @throws MissingResourceException
	 */
	private void setRootTemplate() throws MissingResourceException {
		for( Entry<String, TemplateModel> entry: this.templates.entrySet()){
			TemplateModel templateModel = entry.getValue();
			if( templateModel.hasRootElement() ){
				this.rootTemplate = templateModel;
			}
		}
		if( this.rootTemplate == null){
			throw new MissingResourceException("Not Root template in the VOTable");
		}
	}
		
	public TemplateModel getRootElement() throws MissingResourceException{
		if( this.rootTemplate == null){
			throw new MissingResourceException("Not Root template in the VOTable");
		} else {
			return this.rootTemplate;
		}

	}
	/**
	 * Resolve references in all {@linkplain Instance}s having a ref
	 * Instances with a ref are replaces with these having the matching ID
	 * Instances with unresolved references are let unchanged
	 * @throws Exception
	 */
	private void resolveReferences() throws Exception {
		Set<MappingElement> refToSolve = this.rootTemplate.getElementsWithRef();
		for( MappingElement mappingElement: refToSolve){
			MappingElementRef mappingElementRef = mappingElement.getElementRef();
			if( mappingElementRef.isByElementRef()){
				for( ElementWithId elementWithId: this.getElementWithId(mappingElementRef.elementRef) ){
					this.rootTemplate.replaceElement(mappingElement, elementWithId.element);
				}
			} else {
				throw new UnsupportedFeatureException("Reference by tableref are not supported yet");
			}
		}	
	}
	
	/**
	 * Return the set of {@linkplain ElementWithId} referencing all elements having a references
	 * TODO checking that simple types are not taken
	 * @param elementRef
	 * @return
	 * @throws Exception
	 */
	private Set<ElementWithId> getElementWithId(String elementRef) throws Exception{
		Set<ElementWithId> retour = new HashSet<>();
		for( Entry<String, TemplateModel> entry: this.templates.entrySet() ){
			TemplateModel templateModel = entry.getValue();
			List<MappingElement> elementsWithId = templateModel.getElementsById(elementRef);
			if( elementsWithId.size() > 1) {
				throw new UnconsistantResourceException("Template " + entry.getKey()
				        + " has " + elementsWithId.size() + " elements with the same ID (" 
						+  elementRef + ")");
			} else if( elementsWithId.size() == 1 ){
				retour.add(new ElementWithId(elementsWithId.iterator().next(), entry.getKey()));
			}
		}
		return retour;
	}

	/**
	 * Return all implemented models in the VODML block
	 * TODO implement it
	 * @return
	 */
	public Set<ModelRef> getImplementedModels(){
		return null;	
	}

	/**
	 * Return true if the model named modelName is referenced in the VODML block
	 * TODO implement it
	 * @param modelName
	 * @return
	 */
	public boolean implementModel(String modelName) {
		// while models are not taken into account
		return true;
	}

	/**
	 * return a flat collection of all mapping element having the dmrole
	 * {@link MappingElement} are returned out of their initial context. 
	 * If more han one is returned, there is no way to know which parent they have
	 * TODO put a back-link in the ModelElement
	 * @param dmrole
	 * @return
	 * @throws Exception 
	 */
	public List<MappingElement> getNodesByRole(String dmrole) throws Exception{
		return this.rootTemplate.getElementsByRole(dmrole);
	}
	/**
	 * return the first mapping element having the dmrole
	 * @param dmrole
	 * @param strict an exception is risen when true and there is more than one matching node
	 * @return
	 * @throws Exception 
	 */
	public MappingElement getFirstNodeWithRole(final String dmrole, final boolean strict) throws Exception{
		List<MappingElement> mes = this.getNodesByRole(dmrole);
		int size;
		if( (size = mes.size()) > 1 ){
			if( strict ){
				throw new UnconsistantResourceException("There are more than 1 element with role=" + dmrole + " cannot get one");
			} else {
				return mes.get(0);
			}
		} else if( size == 0 ){
			throw new UnconsistantResourceException("There are no element with role=" + dmrole + " cannot get one");
		} else {
			return mes.get(0);
		}
	}

	/**
	 * return the first mapping element having the dmrole.
	 * If there are multiple nodes matching the role, the first one is returned, 
	 * it can be anyone of set
	 * @param dmrole
	 * @return
	 * @throws Exception 
	 */
	public MappingElement getFirstNodeWithRole(String dmrole) throws Exception{
		return this.getFirstNodeWithRole(dmrole, false);
	}

	/**
	 * Inner class modeling a {@link MappingElement} having an ID
	 * @author laurentmichel
	 *
	 */
	/**
	 * @author laurentmichel
	 *
	 */
	class ElementWithId{
		/**
		 * Reference on the mapping element
		 */
		public final MappingElement element;
		/**
		 * Name of the template the mapping element belongs to
		 */
		public final String template;
	
		/**
		 * Constructor
		 * @param element
		 * @param template
		 */
		public ElementWithId(MappingElement element, String template) {
			super();
			this.element = element;
			this.template = template;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString(){
			return "template: " + this.template + " element: " + this.element.toString();
		}
	}
}
