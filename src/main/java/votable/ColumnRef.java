/**
 * 
 */
package votable;

import java.io.Serializable;

/**
 * IN the mapping-lite, <COLLECTION> cells can be set wyth either values read in
 * <TABLEDATA> (BY_POS) mode or in a <PARAM> or somewhere 
 * in the <VODML> block (BY_VAL mode).
 * In this case, the value will be the same for all row instance.
 * These 2 way of setting table values modeled this class
 * @author michel
 *
 */
public class ColumnRef implements Serializable{
	/**
	 * requested for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Avatar denoting that data must be read in the <TABLEDATA>
	 */
	public static final int BY_POS=1; 
	/**
	 * Avatar denoting that data must be read in a <PARAM>
	 */
	public static final int BY_VAL=2;
	
	/**
	 * dmrole of the quantity referenced by this object
	 */
	public  final String role;
	/**
	 * Acces mode (BY_POS or BY_VAL)
	 */
	public  final int mode;
	/**
	 * Number of the column (for BY_POS mode only)
	 */
	public  final int index;
	/**
	 * Value read in a param or somewhere in the <VODML> block (for BY_VAL mode only)
	 */
	public  final String value;
	
	/**
	 * Constructor for the BY_POS mode
	 * @param index column number
	 * @param role dmrole of the column
	 */
	ColumnRef(int index, String role){
		this.index = index;
		this.value = null;
		this.role = role;
		this.mode = BY_POS;
	}
	
	/**
	 * Constructor for the BY_VAL mode
	 * @param value read in a <PARAM> or somewhere in the <VODML> block
	 * @param role
	 */
	ColumnRef(String value, String role){
		this.index = -1;
		this.value = value;
		this.role = role;
		this.mode = BY_VAL;
	}
	
	/**
	 * @return true if the instance is in the BY_VAL mode
	 */
	public boolean isByValue(){
		return (this.mode == BY_VAL);
	}
	/**
	 * @return true if the instance is in the BY_POS mode
	 */
	public boolean isByPos(){
		return (this.mode == BY_POS);
	}

	@Override
	public String toString(){
		if( this.mode == BY_POS) {
			return "ColumnRef BY_POS index=" + this.index; 
		} else {
			return "ColumnRef BY_VAL value=" + this.value; 

		}
	}
}
