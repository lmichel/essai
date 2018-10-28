/**
 * 
 */
package mapping.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import exceptions.MappingClassException;
import mapping.MappingElement;
import mapping.MappingElementRef;
import mapping.nodes.MappingNodeId;
import votable.TableModel;

/**
 * Super class for all mapping elements without {@link MappingNodeId}
 * The subclasses roughly match the VO-DML types
 * @author michel
 *
 */
public abstract class Type extends MappingElement {

	/* (non-Javadoc)
	 * @see mapping.MappingElement#addElement(java.lang.String, mapping.MappingElement)
	 */
	@Override
	public void addMappingElement(final String key, final MappingElement mappingElement) throws Exception{
		throw new MappingClassException("No element can be added to an instance of " + this.getClass());
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#getValue(java.lang.String)
	 */
	@Override
	public MappingElement getContentElement(final String key)  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return an Element by key");
	}
	/* (non-Javadoc)
	 * @see mapping.MappingElement#getValue(int)
	 */
	@Override
	public MappingElement getContentElement(final int index)  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return an Element index");
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getElementRef()
	 */
	@Override
	public MappingElementRef getElementRef() throws MappingClassException{
		return null;
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#getLength()
	 */
	@Override
	public int getLength()  throws Exception{
		return -1;
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#getKeySet()
	 */
	@Override
	public List<String> getKeySet()  throws Exception{
		return null;
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#isLeaf()
	 */
	@Override
	public boolean isLeaf()  throws Exception{
		return false;
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#getStringValue()
	 */
	@Override
	public String getStringValue()  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return en String value");
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#getNumericalValue()
	 */
	@Override
	public double getNumericalValue()  throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not return en Numerical value");
	}
	
	/* (non-Javadoc)
	 * @see mapping.MappingElement#getChildByRole(java.lang.String)
	 */
	@Override
	public MappingElement getFirstChildByRole(final String role) throws Exception{
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not select child by role");
	}
	
	/* (non-Javadoc)
	 * @see mapping.MappingElement#getSubelementsByRole(java.lang.String)
	 */
	@Override
	public List<MappingElement> getSubelementsByRole(final String role)  throws Exception{
		return new ArrayList<>();
	}
	/* (non-Javadoc)
	 * @see mapping.MappingElement#getSubelementsByClass(java.lang.Class)
	 */
	@Override
	public List<MappingElement> getSubelementsByClass(Class classe) throws Exception{
		return new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see main.mapping.MappingElement#getSubelementsWithRef()
	 */
	@Override
	public List<MappingElement> getSubelementsWithRef() throws Exception{
		return new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see mapping.MappingElement#getNodeId()
	 */
	@Override
	public MappingNodeId getNodeId() throws Exception{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see mapping.MappingElement#resolveValueByReference(mapping.votable.TableModel)
	 */
	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		throw new MappingClassException("An instance of " + this.getClass() + " Cannot not resolve value by reference");
	}

}
