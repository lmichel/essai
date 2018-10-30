/**
 * 
 */
package mapping.types;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mapping.MappingElement;
import votable.TableModel;

/**
 * A Tuple is a set of key/value pairs
 * Key are string and value are instance of {@link MappingElement} sublcasses
 * @author michel
 *
 */
public class Tuple extends Type{
	/**
	 * Set of  key/value pairs
	 */
	protected Map<String, MappingElement> map = new LinkedHashMap<>();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for(Entry<String, MappingElement> entry: this.map.entrySet()){
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(entry.getValue());
			sb.append(' ');
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#emptyData()
	 */
	@Override 
	public void emptyData() throws Exception {
		this.map = new LinkedHashMap<>();
	}
	/* (non-Javadoc)
	 * @see mapping.types.Type#addElement(java.lang.String, mapping.MappingElement)
	 */
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement){
		this.map.put(key, mappingElement);
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getValue(java.lang.String)
	 */
	@Override
	public MappingElement getContentElement(final String key) {
		return this.map.get(key);
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getKeySet()
	 */
	@Override
	public List<String> getKeySet() {
		return new ArrayList<>(this.map.keySet());
	}

	/* (non-Javadoc)
	 * @see main.mapping.types.Type#getLength()
	 */
	@Override
	public int getLength(){
		return this.map.size();
	}
	/* (non-Javadoc)
	 * @see mapping.types.Type#getChildByRole(java.lang.String)
	 */
	@Override
	public MappingElement getFirstChildByRole(String role) throws Exception{
		MappingElement retour = null;
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			if( !element.isLeaf() && element.getNodeId().hasRole(role)){
				retour = element;
				break;
			}
		}
		return retour;
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getSubelementsByRole(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByRole(String role) throws Exception {
		final Set<MappingElement> retour = new LinkedHashSet<>();
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			if( !element.isLeaf() ){
				if( element.getNodeId().hasRole(role)){
					retour.add(element);
				}
				retour.addAll(element.getSubelementsByRole(role));
			} else if (key.equalsIgnoreCase(role)){
				retour.add(element);				
			}
		}
		return new ArrayList<>(retour);
	}
	
	@Override
	public List<MappingElement> getSubelementsByVodmlId(String vodmlId) throws Exception{
		final Set<MappingElement> retour = new LinkedHashSet<>();
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			if( !element.isLeaf() ){
				if( element.hasVodmlId(vodmlId)){
					retour.add(element);
				}
				retour.addAll(element.getSubelementsByVodmlId(vodmlId));
			} else if (key.equalsIgnoreCase(vodmlId)){
				retour.add(element);				
			}
		}
		return new ArrayList<>(retour);
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getSubelementsByClass(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<MappingElement> getSubelementsByClass(Class classe) throws Exception {
		final Set<MappingElement> retour = new LinkedHashSet<>();
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			if( element.getClass().equals(classe) ){
				retour.add(element);
			}
			retour.addAll(element.getSubelementsByClass(classe));
		}
		return new ArrayList<>(retour);
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsWithref()
	 */
	@Override
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		final Set<MappingElement> retour = new LinkedHashSet<>();
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			if( element.getElementRef() != null ){
				retour.add(element);
			}
			retour.addAll(element.getSubelementsWithRef());
		}
		return new ArrayList<>(retour);
	}


	/* (non-Javadoc)
	 * @see mapping.types.Type#resolveValueByReference(mapping.votable.TableModel)
	 */
	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			element.setValuesFromTableModel(tableModel);
		}
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#replaceElement(main.mapping.MappingElement, main.mapping.MappingElement)
	 */
	@Override
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		for(final String key: this.getKeySet()){
			if( this.getContentElement(key) == formerElement){
				this.map.put(key, newElement);
				return;
			}
			this.getContentElement(key).replaceElement(formerElement,newElement);
		}

	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getAtomicTypeByValue(java.lang.String)
	 */
	@Override
	public List<String> getKeysOfAtomicTypeByValue(String startWith) throws Exception {
		final List<String> retour = new ArrayList<>();
		for(final String key: this.getKeySet()){
			final MappingElement element = this.getContentElement(key);
			if( element instanceof Textual && element.getStringValue().startsWith(startWith)  ){
				retour.add(key);
			}
			retour.addAll(element.getKeysOfAtomicTypeByValue(startWith));
		}
		return retour;
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getAtomicTypeByValue(java.lang.String)
	 */
	@Override
	public void setAtomicTypeValue(String key, String value) throws Exception {
		MappingElement me;
		if( (me = this.getContentElement(key)) != null ) {
			if( me instanceof Textual ){
				this.map.put(key, new Textual(value));
			} 
		}
		for(final String lkey: this.getKeySet()){
			this.getContentElement(lkey).setAtomicTypeValue(key, value);
		}
	}


}
