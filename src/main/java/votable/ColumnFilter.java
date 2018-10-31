/**
 * 
 */
package votable;

import java.io.Serializable;

import mapping.MappingElement;
import mapping.types.Array;

/**
 * Filter on a set of data read TABLEDATA row
 * @author michel
 *
 */
public class ColumnFilter implements Serializable {
	/**
	 * requested for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Reference of the column which values are filtered
	 * Always in BY_POS mode by construction
	 */
	private final ColumnRef columnRef;
	/**
	 * Only rows whose specified column value matches operand are selected 
	 */
	private final String operand;
	
	/**
	 * @param colIndex index of the column on which the filter is applied
	 * @param operand
	 */
	public ColumnFilter(final int colIndex, final String operand){
		this.columnRef = new ColumnRef(colIndex, "norole");
		this.operand = operand;
	}
	
	/**
	 * Check that the dataRow matches the filter
	 * @param dataRow
	 * @return
	 */
	public boolean checkDataRow(final Array<MappingElement> dataRow) {
		final String value = dataRow.getContentElement(this.columnRef.index).toString();
		return  this.operand.equalsIgnoreCase(value) ;
	}
}
