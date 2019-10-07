package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import exceptions.MappingClassException;
import exceptions.MissingResourceException;
import exceptions.UnconsistantResourceException;
import exceptions.UnsupportedFeatureException;
import mapping.GroupByProcessor;
import mapping.MappingElement;
import mapping.MappingFilter;
import mapping.nodes.DataTableCollection;
import mapping.nodes.GroupByCollection;
import mapping.nodes.Instance;
import mapping.nodes.MultiInstanceCollection;
import mapping.types.Textual;


/**
 * Parser of the VODML block
 * - Build a set of {@link TemplateModel}, one for each annotated table
 * - Does not read the VOTABLE
 * - Does not set the values
 * - Does not resolve cross-template references
 * - Does not read GLOBALS
 * - Does not support TEMPLATES with mode the one child
 * TODO parse TEMPLATES without dmrol=root element
 * TODO parse TEMPLATES with mutiple children
 * 
 * @author laurentmichel
 *
 */
public class VodmlBlockParser {
	/**
	 * Avatar for COLLECTION
	 */
	public static final String COLLECTION ="COLLECTION";
	public static final String SET ="SET";
	public static final String ARRAY ="ARRAY";
	public static final String COMPOSITION ="COMPOSITION";
	/**
	 * Avatar for INSTANCE
	 */
	public static final String INSTANCE ="INSTANCE";
	/**
	 * Avatar for VALUE
	 */
	public static final String VALUE ="VALUE";
	/**
	 */
	public static final String FILTER ="FILTER";
	/**
	 * Avatar for SET... groupby=... 
	 */
	public static final String GROUPBY ="groupby";
	/**
	 * Root of XML root node (internal use)
	 */
	private Element documentRoot;
	/**
	 * XPath engine used here and there
	 */
	private   XPath xpath ;	
	/**
	 * Indentation level to make the logging readable
	 */
	private int indent=0;
	/**
	 * Map of parsed template. Key are the TABLE ID
	 */
	public Map<String, TemplateModel> templates = new LinkedHashMap<>();

	/**
	 * Construtor based on a filename
	 * @param filename
	 * @throws Exception
	 */
	public VodmlBlockParser(final String filename) throws Exception{
		final InputStream stream = new FileInputStream((new File(filename)));
		this.initParser(stream);
	}

	/**
	 * Construtor based on a InputStream
	 * @param stream
	 * @throws Exception
	 */
	public VodmlBlockParser(final InputStream stream)throws Exception{
		this.initParser(stream);
	}

	/**
	 * Init internal attribute and points on document root
	 * @param stream
	 * @throws Exception
	 */
	private void initParser(final InputStream stream) throws Exception{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document xml = builder.parse(stream);
		this.documentRoot = xml.getDocumentElement();
		final XPathFactory xpf = XPathFactory.newInstance();
		this.xpath = xpf.newXPath();
	}

	/**
	 * Build a space string matching the indentation level
	 * @return
	 */
	private String getIndentSpace(){
		String retour = "";
		for( int i=0; i<this.indent ; i++){
			retour += "    ";
		}
		return retour;
	}

	/**
	 * Print put an parser message with the appropriate indentation level
	 * @param message
	 */
	private void printParsedNode(String message){
		System.out.println(this.getIndentSpace() + message);
	}

	/**
	 * @param node
	 * @return return the dmrole attribute of node or null
	 */
	private String getDmRole(final Node node) {
		return this.getNodeAttribute(node, "dmrole");
	}

	/**
	 * @param node
	 * @param attribute
	 * @return return the attribute value of node or null
	 */
	private String getNodeAttribute(final Node node, final String attribute) {
		if( node.getAttributes() != null ) {
			final Node attNode = node.getAttributes().getNamedItem(attribute);
			if( attNode != null ){
				return attNode.getNodeValue();
			} 
		}
		return null;
	}

	/**
	 * Add a MappingElementRef to newElement if there is either a ref or a tableref attribute in node
	 * @param newElement
	 * @param instanceNode
	 * @throws MappingClassException 
	 */
	private void setElementRef(MappingElement newElement, Node node) throws MappingClassException {
		String elementRef = this.getNodeAttribute(node, "ref");
		if( elementRef != null ){
			newElement.setElementRef(elementRef);
		} else {
			elementRef = this.getNodeAttribute(node, "tablreref");
			if( elementRef != null ){
				newElement.setElementRef(elementRef, newElement.getVodmlRole());
			}
		}
	}

	/**
	 * @param nodeName
	 * @return tru id name is one of the 4 supported tags for collections
	 */
	private boolean isCollectionTag(String nodeName){
		return (nodeName.equalsIgnoreCase(COLLECTION) ||
				nodeName.equalsIgnoreCase(COMPOSITION) ||
				nodeName.equalsIgnoreCase(SET) ||
				nodeName.equalsIgnoreCase(ARRAY));
	}
	/**
	 * Parse a TEMPLATES block and add it to stored template set
	 * In the current version, we supposed that the template has only one root element
	 * TODO split in simpler methods
	 * @param templateNode TEMPLATES XML node
	 * @throws Exception 
	 */
	private void parseTemplate(final Node templateNode) throws Exception{
		final String tableRef = this.getNodeAttribute(templateNode, "tableref");
		this.printParsedNode("template " + tableRef);
		final List<MappingElement> templateMapping = new ArrayList<>();
		final TemplateModel templateModel = new TemplateModel(tableRef, templateMapping);
		this.templates.put(tableRef,templateModel );
		this.indent++;
		MappingElement mappingRoot = null;
		final NodeList list = templateNode.getChildNodes();
		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			String childRole = this.getDmRole(child);
			if( child.getNodeName().equalsIgnoreCase(INSTANCE)  ) {
				mappingRoot = this.parseInstance(child, null);
				templateMapping.add(mappingRoot);
				if( "root".equalsIgnoreCase(childRole)) {
					templateModel.setRootElement(mappingRoot);
				}
			} else if( this.isCollectionTag(child.getNodeName()) ) {
				mappingRoot = this.parseCollection(child, null);
				if( "root".equalsIgnoreCase(childRole)) {
					String gbNode = this.getNodeAttribute(child, GROUPBY);
					if( gbNode == null ){
						throw new UnconsistantResourceException("A root collection must have a groupby attribute ");
					} else {
						templateModel.setRootElement(mappingRoot);
						templateModel.setGroupByProcessor(new GroupByProcessor((GroupByCollection) mappingRoot));						
					}
					break;
				} else {
					mappingRoot = this.parseCollection(child, null);
				}
				templateMapping.add(mappingRoot);
			} else if( !child.getNodeName().startsWith("#") ){
				throw new UnsupportedFeatureException("Unsupported node " + child.getNodeName());
			}
		}
		this.indent--;

		if( templateModel.hasRootElement() && templateModel.getLength() != 1 ){
			throw new MissingResourceException("A root template (" + tableRef + ") can have only one child (" + templateModel.getLength() + " found)");
		}
	}

	/**
	 * Parse the GLOBALS block and add it to stored template set
	 * @param templateNode TEMPLATES XML node
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private void parseGlobals(final Node templateNode) throws Exception{
		final String tableRef = "globals";
		this.printParsedNode("template globals");
		final List<MappingElement> templateMapping = new ArrayList<>();
		final TemplateModel templateModel = new TemplateModel(tableRef, templateMapping);
		this.indent++;
		MappingElement mappingRoot = null;
		final NodeList list = templateNode.getChildNodes();
		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			final String childName = child.getNodeName();
			if( childName.equalsIgnoreCase(INSTANCE)  ) {
				mappingRoot = this.parseInstance(child, null);
				templateMapping.add(mappingRoot);
				this.templates.put(tableRef,templateModel );
			} else if( childName.equalsIgnoreCase(COLLECTION) || childName.equalsIgnoreCase(VALUE)) {
				throw new UnconsistantResourceException("GLOBALS block can not have a " + childName+ " as child");
			} 
		}
		this.templates.put(tableRef,templateModel );

		this.indent--;

	}


	/**
	 * Parse the INSTANCE node and add it to mappingElement 
	 * @param instanceNode INSTANCE node
	 * @param mappingElement Parent of instanceNode
	 * @return
	 * @throws Exception
	 */
	private Instance parseInstance(final Node instanceNode, MappingElement mappingElement) throws Exception{
		final String role = this.getDmRole(instanceNode);
		this.printParsedNode("instance " + role);

		String id = this.getNodeAttribute(instanceNode, "ID");		
		final Instance newElement;
		if( id == null ){
			newElement = new Instance(role);
		} else {
			newElement = new Instance(id, role);
		}
		this.indent++;
		final NodeList list = instanceNode.getChildNodes();
		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			if( child.getNodeName().equalsIgnoreCase(INSTANCE)) {
				this.parseInstance(child, newElement);
			} else if( child.getNodeName().equalsIgnoreCase(VALUE)) {
				this.parseValue(child, newElement);
			} else if( this.isCollectionTag(child.getNodeName()) ) {
				this.parseCollection(child, newElement);
			} else if( child.getNodeName().equalsIgnoreCase(FILTER)) {
				MappingFilter mappingFilter = this.parseFilter(child);
				mappingElement.setMappingFilter(mappingFilter);
			} 	
		}
		if( mappingElement != null ){
			mappingElement.addMappingElement(role, newElement);
		}
		this.indent--;
		this.setElementRef(newElement, instanceNode);
		return newElement;
	}


	/**
	 * Parse the FILTER node and give it to mappingElement 
	 * @param filterNode FILTER xml node
	 * @return a {@link MappingFilter} instance
	 * @throws Exception
	 */
	private MappingFilter parseFilter(final Node filterNode) throws Exception {
		String key = filterNode.getAttributes().getNamedItem("key").getNodeValue();
		String value = filterNode.getAttributes().getNamedItem("value").getNodeValue();
		if( key == null || value == null || key.length()  == 0 || value.length() == 0 ){
			throw new UnconsistantResourceException("Missing key or value in a FILTER node");
		}
		return new MappingFilter(key, value);
	}
	/**
	 * Parse the COLLECTION node and add it to mappingElement 
	 * @param instanceNode COLLECTION node
	 * @param mappingElement Parent of instanceNode
	 * @throws Exception 
	 */
	private MappingElement parseCollection(final Node instanceNode, MappingElement mappingElement) throws Exception{
		final String role = this.getDmRole(instanceNode);
		DataTableCollection newElement = null;
		MappingElement retour;
		if( instanceNode.getNodeName().equalsIgnoreCase(COMPOSITION) || 
				instanceNode.getAttributes().getNamedItem("arraysize") != null ||
				instanceNode.getAttributes().getNamedItem("size") != null){
			this.printParsedNode("collection-multiinstance " + role);
			this.indent++;
			retour =  this.parseMultiInstanceCollection(instanceNode, mappingElement);
		} else if( instanceNode.getNodeName().equalsIgnoreCase(SET) ) {
			if( instanceNode.getAttributes().getNamedItem(GROUPBY) == null){
				throw new UnconsistantResourceException("SET must have a GROUPBY attribute (role=" + role + ")");
			} else {
				this.printParsedNode("collection-groupby " + role);
				this.indent++;
				retour =   this.parseGroupByCollection(instanceNode, mappingElement);
			}
		} else {
			this.printParsedNode("collection-datatable " + role);
			this.indent++;
			retour = this.parseDataTableCollection(instanceNode, mappingElement);
		}
		this.indent--;
		this.setElementRef(newElement, instanceNode);
		return retour;
	}

	/**
	 * @param instanceNode
	 * @param mappingElement
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private MappingElement parseDataTableCollection(final Node instanceNode, MappingElement mappingElement) throws Exception{
		final String role = this.getDmRole(instanceNode);
		final NodeList list = instanceNode.getChildNodes();
		DataTableCollection newElement = null;
		String id = this.getNodeAttribute(instanceNode, "ID");		

		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			final String childName = child.getNodeName();
			if( childName.equalsIgnoreCase(INSTANCE)) {
				newElement = new DataTableCollection (id, role);
				newElement.addValue(this.parseInstance(child, newElement));
				/*
				 * Collection in TABLE  mode can only have one children
				 */
				break;
			} else if(childName.equalsIgnoreCase(COLLECTION) || childName.equalsIgnoreCase(VALUE) ){
				throw new UnsupportedFeatureException("A data table collection must have an INSTACE as child");
			} 
		}
		if( newElement == null ){
			throw new MissingResourceException("Data Table Collection with role " + role + " has no valid children");
		} else {
			if( mappingElement != null ) {
				mappingElement.addMappingElement(role, newElement);
			}
		}
		this.setElementRef(newElement, instanceNode);
		return newElement;
	}

	/**
	 * @param instanceNode
	 * @param mappingElement
	 * @return
	 * @throws Exception
	 */
	private MappingElement parseGroupByCollection(final Node instanceNode, MappingElement mappingElement) throws Exception{
		final String role = this.getDmRole(instanceNode);
		Node gbNode = instanceNode.getAttributes().getNamedItem(GROUPBY);
		final NodeList list = instanceNode.getChildNodes();
		MappingElement newElement = null;
		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			final String childName = child.getNodeName();
			if( childName.equalsIgnoreCase(INSTANCE)) {
				newElement = new GroupByCollection(role, gbNode.getNodeValue());
				this.parseInstance(child, newElement);
				/*
				 * Collection in TABLE or GROUPBY mode can only have one children
				 */
				break;
			} 
		}
		if( newElement == null ){
			throw new MissingResourceException("GroupByCollection with role " + role + " has no valid children");
		} else {
			if( mappingElement != null ) {
				mappingElement.addMappingElement(role, newElement);
			}
		}
		this.setElementRef(newElement, instanceNode);
		return newElement;
	}

	/**
	 * Parse the COLLECTION node and add it to mappingElement 
	 * The values won't be read in TABLEDATA but as a set in INSTANCE enclosed
	 * in COLLECTION
	 * @param instanceNode COLLECTION node
	 * @param mappingElement Parent of instanceNode
	 * @throws Exception
	 */
	private MappingElement parseMultiInstanceCollection(final Node instanceNode, MappingElement mappingElement) throws Exception{
		final String role = this.getDmRole(instanceNode);
		this.printParsedNode("multi-instance collection " + role);
		this.indent++;
		final NodeList list = instanceNode.getChildNodes();
		MultiInstanceCollection<Instance> newElement = null;
		String id = this.getNodeAttribute(instanceNode, "ID");		

		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			final String childName = child.getNodeName();
			if( childName.equalsIgnoreCase(INSTANCE)) {
				if( newElement == null ){
					newElement = new MultiInstanceCollection<>(id, role);
					mappingElement.addMappingElement(role, newElement);
				}
				this.parseInstance(child, newElement);
			} else if( childName.equalsIgnoreCase(COLLECTION)) {
				throw new UnsupportedFeatureException("Collections of collections not supported yet in the MultiInstance context");
			} else if( !childName.equalsIgnoreCase("#text") && !childName.equalsIgnoreCase("#comment") ){
				throw new UnsupportedFeatureException("MultiInstance collection (" + role + ") only support INSTANCES or COLLECTION " + childName);
			}
		}
		this.indent--;
		this.setElementRef(newElement, instanceNode);
		return newElement;
	}



	/**
	 * Parse the VALUE node and add it to mappingElement 
	 * @param instanceNode VALUE node
	 * @param mappingElement Parent of instanceNode
	 * @throws Exception
	 */
	private MappingElement parseValue(final Node instanceNode, MappingElement mappingElement) throws Exception{
		final String role = this.getDmRole(instanceNode);
		this.printParsedNode("value " + this.getDmRole(instanceNode));
		final MappingElement newElement ;
		Node val = null;
		if( (val = instanceNode.getAttributes().getNamedItem("value")) != null ) {
			newElement = new Textual(val.getNodeValue());
		} else if( (val = instanceNode.getAttributes().getNamedItem("ref")) != null ) {
			newElement = new Textual("@" + val.getNodeValue());
		} else {
			throw new UnconsistantResourceException("Value with role " + role + " has neither value not ref");
		}
		this.indent++;
		final NodeList list = instanceNode.getChildNodes();
		for( int i=0 ; i<list.getLength() ; i++){
			final Node child = list.item(i);
			if( child.getNodeName().equalsIgnoreCase(INSTANCE)) {
				this.parseInstance(child, newElement);
			}	
		}
		mappingElement.addMappingElement(role, newElement);
		this.indent--;
		return newElement;
	}

	/**
	 * Entry point of the TreeBuilder. 
	 * Parse the TEMPLATES one by onee
	 * @throws Exception
	 */
	public void parse() throws Exception {
		final Node node = (Node)this.xpath.evaluate("//VODML", this.documentRoot, XPathConstants.NODE);
		if( node == null ){
			throw new MissingResourceException("No VODML element found");
		} else {
			final NodeList nodelist = (NodeList)this.xpath.evaluate("//TEMPLATES", node, XPathConstants.NODESET);
			if( nodelist.getLength() == 0 ){
				throw new MissingResourceException("No TEMPLATES element found");
			} else {
				for( int i=0 ; i<nodelist.getLength() ; i++ ){
					this.parseTemplate(nodelist.item(i));
				}
			}
			final NodeList globals = (NodeList)this.xpath.evaluate("//GLOBALS", node, XPathConstants.NODESET);
			for( int i=0 ; i<globals.getLength() ; i++ ){
				this.parseGlobals(globals.item(i));
			}
		}
	}

}
