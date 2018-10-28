/**
 * 
 */
package mapping.types;

import mapping.MappingElement;
import votable.TableModel;

/**
 * {@link Type} container for Strings
 * @author michel
 *
 */
public class Textual extends Type{
	/**
	 * content is a simple String
	 */
	private String content;
	
	/**
	 * @param Content
	 */
	public Textual(final String content) {
		super();
		this.content = content;
	}
	
	/**
	 * Store a double as String
	 * @param Content
	 */
	public Textual(final double content) {
		super();
		this.content = Double.toString(content);
	}
	
	/**
	 * Store a long as String
	 * @param Content
	 */
	public Textual(final long content) {
		super();
		this.content = Long.toString(content);
	}
	
	public void setValue(String value){
		this.content = value;
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getStringValue()
	 */
	@Override
	public String getStringValue() {
		return this.content;
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.content;
	}
	
	/* (non-Javadoc)
	 * @see mapping.types.Type#resolveValueByReference(mapping.votable.TableModel)
	 */
	@Override
	public void setValuesFromTableModel(TableModel tableModel) throws Exception  {
		if( this.content.startsWith("@")){
			String ref = this.content.substring(1);
			this.content = tableModel.getParamValueByIdOrName(ref);
		}
	}


}
