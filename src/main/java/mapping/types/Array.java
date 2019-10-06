/**
 * 
 */
package mapping.types;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import mapping.MappingElement;

/**
 * {@link Type} container for arrays
 * The array value must all be instance is the same class (subclass of {@link MappingElement})
 * set as generic
 * @author michel
 *
 */
/**
 * @author laurentmichel
 *
 * @param <T>
 */
/**
 * @author laurentmichel
 *
 * @param <T>
 */
public class Array<T extends MappingElement > extends Type{
	/**
	 * Content
	 */
	protected ArrayList<T> values = new ArrayList<>();


	/**
	 * @param value
	 */
	public void addValue(final T value){
		this.values.add(value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		final StringBuilder sbuff = new StringBuilder();
		for( final MappingElement val : this.values ){
			sbuff.append("  ");
			sbuff.append(val.toString());
		}
		return sbuff.toString();
	}


	/* (non-Javadoc)
	 * @see mapping.types.Type#addElement(java.lang.String, mapping.MappingElement)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement){
		this.values.add((T) mappingElement);
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getValue(int)
	 */
	@Override
	public MappingElement getContentElement(final int index) {
		return this.values.get(index);
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getLength()
	 */
	@Override
	public int getLength() {
		return this.values.size();
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getChildByRole(java.lang.String)
	 */
	@Override
	public MappingElement getFirstChildByRole(String role) throws Exception{
		final int alength = this.getLength();
		MappingElement retour = null;
		for( int i=0 ; i<alength ; i++){
			final MappingElement el = this.getContentElement(i);
			if( el.isLeaf()){
				break;
			} else if( el.getNodeId().hasRole(role)){
				retour = el;
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
		for(final MappingElement element: this.values){

			if( !element.isLeaf() && element.getNodeId() != null){
				if( element.getNodeId().hasRole(role)){
					retour.add(element);
				}
				retour.addAll(element.getSubelementsByRole(role));
			}
		}
		return new ArrayList<>(retour);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByVodmlId(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByVodmlId(String vodmlId) throws Exception{
		final Set<MappingElement> retour = new LinkedHashSet<>();
		for(final MappingElement element: this.values){
			if( !element.isLeaf() && element.getNodeId() != null){
				if( element.hasVodmlId(vodmlId) ){
					retour.add(element);
				}
				retour.addAll(element.getSubelementsByVodmlId(vodmlId));
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
		for(final MappingElement element: this.values){
			if( element.getClass().equals(classe)) {
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
		for(final MappingElement element: this.values){
			if( element.getElementRef() != null) {
				retour.add(element);
			}
			retour.addAll(element.getSubelementsWithRef());
		}
		return new ArrayList<>(retour);
	}
	
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#replaceElement(main.mapping.MappingElement, main.mapping.MappingElement)
	 */
	@Override
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		int index=0;
		for(final MappingElement element: this.values){
			if( element == formerElement){
				this.values.set(index, (T) newElement);
				return;
			}
			index++;
		}
		for(final MappingElement element: this.values){
			element.replaceElement(formerElement, newElement);
		}
	}
}
