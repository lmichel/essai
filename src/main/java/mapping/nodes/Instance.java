package mapping.nodes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import exceptions.UnconsistantResourceException;
import mapping.MappingElement;
import votable.TableModel;
import mapping.types.Tuple;

/**
 * Model for VO-DML object containing a set of key/value pairs ({@link Tuple})
 * @author laurentmichel
 *
 */
public class Instance extends MappingNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Node content: a {@link Tuple}
	 */
	public final Tuple content = new Tuple();
	
	/**
	 * @param vodmlId
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public Instance(String vodmlId, String role, String name) throws UnconsistantResourceException {
		super(vodmlId, role, name);
	}
	/**
	 * @param vodmlId
	 * @param role
	 * @param name
	 * @throws UnconsistantResourceException 
	 */
	public Instance(String vodmlId, String role) throws UnconsistantResourceException {
		super(vodmlId, role);
	}
	/**
	 * @param role
	 * @throws UnconsistantResourceException 
	 */
	public Instance(String role) throws UnconsistantResourceException {
		super(role);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		final StringBuilder sb = new StringBuilder("INSTANCE ");
		try {
			sb.append(this.getNodeId().toString());
		} catch (Exception e) {}
		sb.append('\n');
		sb.append(this.content.toString());
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see mapping.nodes.MappingNode#addElement(java.lang.String, mapping.MappingElement)
	 */
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement){
		this.content.addMappingElement(key, mappingElement);
	}
	
	/* (non-Javadoc)
	 * @see mapping.nodes.MappingNode#getValue(java.lang.String)
	 */
	@Override
	public MappingElement getContentElement(final String key) {
		return this.content.getContentElement(key);
	}

	/* (non-Javadoc)
	 * @see mapping.nodes.MappingNode#getKeySet()
	 */
	@Override
	public List<String> getKeySet()  throws Exception{
		return new ArrayList<>(this.content.getKeySet());
	}
	
	/* (non-Javadoc)
	 * @see mapping.nodes.MappingNode#getChildByRole(java.lang.String)
	 */
	@Override
	public MappingElement getFirstChildByRole(final String role) throws Exception{
		return this.content.getFirstChildByRole(role);
	}
	
	/* (non-Javadoc)
	 * @see mapping.MappingElement#getSubelementsByRole(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByRole(final String role)  throws Exception{
		return this.content.getSubelementsByRole(role);
	}
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsByVodmlId(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByVodmlId(final String vodmlId)  throws Exception{
		return this.content.getSubelementsByVodmlId(vodmlId);
	}
	/* (non-Javadoc)
	 * @see mapping.MappingElement#getSubelementsByClass(java.lang.Class)
	 */
	@Override
	public List<MappingElement> getSubelementsByClass(final Class classe) throws Exception{
		return this.content.getSubelementsByClass(classe);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsWithref()
	 */
	@Override
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		return this.content.getSubelementsWithRef();
	}

	/* (non-Javadoc)
	 * @see mapping.nodes.MappingNode#resolveValueByReference(mapping.votable.TableModel)
	 */
	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		this.content.setValuesFromTableModel(tableModel);
	}
	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#replaceElement(main.mapping.MappingElement, main.mapping.MappingElement)
	 */
	@Override
	public void replaceElement(MappingElement formerElement, MappingElement newElement) throws Exception  {
		this.content.replaceElement(formerElement, newElement);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getAtomicTypeByValue(java.lang.String)
	 */
	@Override
	public List<String> getKeysOfAtomicTypeByValue(String startWith) throws Exception {
		return this.content.getKeysOfAtomicTypeByValue(startWith);
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getAtomicTypeByValue(java.lang.String)
	 */
	@Override
	public void setAtomicTypeValue(String key, String value) throws Exception {
		this.content.setAtomicTypeValue(key, value);
	}
	
	/**
	 * @param object
	 * @return a deep clone of the object
	 * @throws Exception 
	 */
	public static  Instance deepClone(Instance object) throws Exception{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		ObjectInputStream objectInputStream = new ObjectInputStream(bais);
		return (Instance) objectInputStream.readObject();
	}


}
