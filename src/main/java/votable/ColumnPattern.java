/**
 * 
 */
package votable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.MissingResourceException;

/**
 * Column binding the model ordering of the quantities with the column ordering.
 * This is done by an order list of {@link ColumnRef}
 * Colmumn values ca eithe be read in the TABLEDATA or in a PARAM or somewhere 
 * in the VODML block. In this case the value is the same for all table rows
 * 
 * @author michel
 *
 */
public class ColumnPattern implements Serializable{
	/**
	 * requested for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Orderd list of column references. The ordering in this list denote the model order 
	 * whereas the ranks stored in the {@link ColumnRef} denote the column numbers of the VOTable
	 */
	private final List<ColumnRef> pattern = new ArrayList<>();
	

	/**
	 * Add a column reference in BY_POS mode
	 * @param index
	 * @param role
	 */
	public void addColumnRef(int index, String role){
		this.pattern.add(new ColumnRef(index, role));
	}
	/**
	 * Add a column reference in BY_POS mode without role
	 * @param index
	 */
	public void addColumnRef(int index){
		this.pattern.add(new ColumnRef(index, "no-role"));
	}
	/**
	 * Add a column reference in BY_VAL mode
	 * @param value
	 * @param role
	 */
	public void addColumnRef(String value, String role){
		this.pattern.add(new ColumnRef(value, role));
	}
	/**
	 * Add a column reference in BY_VAL mode without role
	 * @param value
	 */
	public void addColumnRef(String value){
		this.pattern.add(new ColumnRef(value,  "no-role"));
	}
	/**
	 * @return the number of columns referenced by that pattern 
	 */
	public int getLength(){
		return this.pattern.size();
	}
	/**
	 * @param index
	 * @return the column reference having the given index
	 */
	public ColumnRef getColumnRef(int index){
		return this.pattern.get(index);
	}
	/**
	 * Return the column reference having the given role.
	 * Note that the column index of the returned ColumnRef is 
	 * this of the raw data. It  not not this of the mapped data
	 * @param role
	 * @return the column reference having the given role
	 */	
	public ColumnRef getColumRefByRole(String role){
		for( ColumnRef columRef: this.pattern){
			if( columRef.role.equalsIgnoreCase(role)){
				return columRef;
			}
		}
		return null;
	}
	

	/**
	 * Return the index in the ColumnRef list of the instance
	 * of the element matching role.
	 * @param role
	 * @return
	 * @throws MissingResourceException
	 */
	public int getFieldIndexByRole(String role) throws MissingResourceException{
		int cpt = 0;
		for( ColumnRef columRef: this.pattern){
			if( columRef.role.equalsIgnoreCase(role)){
				return cpt;
			}
			cpt++;
		}	
		throw new MissingResourceException("Column Pattern has no column with dmrole=" + role);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		StringBuilder retour = new StringBuilder("col pattern [");
		for(ColumnRef cr : this.pattern){
			retour.append(((cr.isByValue())? ("\"" + cr.value + "\""): cr.index ) +  " ");
		}
		retour.append("]");
		return retour.toString();
	}
	
}
