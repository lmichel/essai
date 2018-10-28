/**
 * 
 */
package mapping;

import java.io.Serializable;

/**
 * Basic model for data filter: a simple key/value pair
 * The value here is a String because the data read in VOTable are string (for ASCII VOTable)
 * Usually the {@link MappingElement} matching both key and value are selected
 * @author michel
 *
 */
public class MappingFilter implements Serializable {
	/**
	 * requested for Serializable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * filter key
	 */
	public  final String key;
	/**
	 * Filter value
	 */
	public  final String value;
	
	/**
	 * @param key
	 * @param value
	 */
	public MappingFilter(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
}
