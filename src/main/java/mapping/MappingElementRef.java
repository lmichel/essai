/**
 * 
 */
package mapping;

import java.io.Serializable;

/**
 * A mapping element can refer to another with 2 different ways. It can either use a ID/ref mechanism
 * or a tableref attribute (to be implemented)
 * In the first case the reference is carried by the elementRef attribute. In the second case it carried
 * by the tableRef attribute. On of them must be null
 * In the second mode, we suppose that the reference point on an element of the targeted table having 
 * the same role as the source element
 * TODO implementation of reference by table
 * @author laurentmichel
 *
 */
public class MappingElementRef  implements Serializable{
	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Table reference used when the reference is a tableref=
	 */
	public final TableRef tableRef;
    /**
     * Element ref used when the reference id ref=
     */
    public final String elementRef;
    
    /**
     * Constructor for the case where  the reference is a tableref=
     * @param tableID
     * @param dmRole
     */
    public MappingElementRef(final String tableID, final String dmRole){
    	this.tableRef = new TableRef(tableID, dmRole);
    	this.elementRef = null;
    }
    /**
     * Constructor for the case where  the reference is a ref=
     * @param elementRef
     */
    public MappingElementRef(final String elementRef){
    	this.tableRef = null;
    	this.elementRef = elementRef;
    }
	
    /**
     * Return true when  the reference is a tableref=
     * @return
     */
    public boolean isByTableRef(){
    	return (this.elementRef == null);
    }
    /**
     * Return true when  the reference is a ref=
     * @return
     */
    public boolean isByElementRef(){
    	return (this.tableRef == null);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return ((this.tableRef == null)? ("byElementRef=" + this.elementRef)
    			: ("byTableRef=[" + this.tableRef + "]"));
    }
    
    /**
     * Inner class handling reference for the tableref= case
     * @author laurentmichel
     *
     */
    class TableRef implements Serializable{
    	/**
    	 * 
    	 */
    	private static final long serialVersionUID = 1L;
    	/**
    	 * ID of the targeted table
    	 */
    	public  final String tableID;
    	/**
    	 * Role of the targeted element
    	 */
    	public  final String dmRole;
    	
    	
    	/**
    	 * Constructor
    	 * @param tableID
    	 * @param dmRole
    	 */
    	public TableRef(String tableID, String dmRole) {
    		this.tableID = tableID;
    		this.dmRole = dmRole;
    	}

    	/* (non-Javadoc)
    	 * @see java.lang.Object#toString()
    	 */
    	@Override
    	public String toString(){
    		return "tableID=" + tableID + " dmRole=" + dmRole;
    	}
    }

}
