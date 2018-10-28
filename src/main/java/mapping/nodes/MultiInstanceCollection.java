/**
 * 
 */
package mapping.nodes;

import java.util.List;
import java.util.Map;

import exceptions.UnconsistantResourceException;
import exceptions.UnsupportedFeatureException;
import mapping.MappingElement;
import votable.TableModel;
import mapping.types.Array;

/**
 * THis MappingElement handle the content of a <COMPOSITION> element
 * The content is an {@link Array} of anything <T>
 * @author laurentmichel
 *
 */
public class MultiInstanceCollection<T extends MappingElement> extends MappingNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Array containing the data of type <T>
	 */
	protected Array<T> content;

	/**
	 * @param vodmlId
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public MultiInstanceCollection(final String vodmlId, final String role, final String name) throws UnconsistantResourceException {
		super(vodmlId, role, name);
	}
	/**
	 * @param vodmlId
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public MultiInstanceCollection(final String vodmlId, final String role) throws UnconsistantResourceException {
		super(vodmlId, role);
	}

	/**
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public MultiInstanceCollection(final String role) throws UnconsistantResourceException {
		super(role);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		for( int i=0 ; i<this.content.getLength() ; i++){
			this.content.getContentElement(i).setValuesFromTableModel(tableModel);
		}
	}

	/**
	 * Add an instance of the <T> class to the content
	 * @param value
	 * @throws UnconsistantResourceException 
	 */
	public void addInstance(final T value) throws UnconsistantResourceException{
		if( this.content == null ){
			this.content = new Array<>();
		}
		this.content.addValue(value);
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#emptyData()
	 */
	@Override 
	public void emptyData() throws Exception {
		this.content = new Array<>();
	}

	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#addMappingElement(java.lang.String, main.mapping.MappingElement)
	 */
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement){
		if( this.content == null ){
			this.content = new Array<>();
		}
		this.content.addMappingElement(key, mappingElement);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsWithref()
	 */
	@Override
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		return this.content.getSubelementsWithRef();
	}


	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#getContentElement(int)
	 */
	@Override
	public MappingElement getContentElement(final int index) {
		return this.content.getContentElement(index);
	}

	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#getLength()
	 */
	@Override
	public int getLength() {
		return this.content.getLength();
	}

	/* (non-Javadoc)
	 * @see main.mapping.nodes.MappingNode#getFirstChildByRole(java.lang.String)
	 */
	@Override
	public MappingElement getFirstChildByRole(final String role) throws Exception{
		return this.content.getFirstChildByRole(role);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByRole(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByRole(final String role) throws Exception{
		return this.content.getSubelementsByRole(role);
	}
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByVodmlId(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByVodmlId(final String vodmlId) throws Exception{
		return this.content.getSubelementsByVodmlId(vodmlId);
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByClass(java.lang.Class)
	 */
	@Override
	public List<MappingElement> getSubelementsByClass(final Class classe) throws Exception{
		return this.content.getSubelementsByClass(classe);
	}


	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#groupContentByColumnValue(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Array> groupContentByColumnValue(String groupByKey) throws Exception{
		throw new UnsupportedFeatureException("Data cannot be grouped in " + this.getClass().getName());

	}
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#replaceElement(main.mapping.MappingElement, main.mapping.MappingElement)
	 */
	@Override
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		this.content.replaceElement(formerElement, newElement);
	}


}
