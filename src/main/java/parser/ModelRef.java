package parser;

/**
 * VODML model reference: just a name and a URL
 * @author laurentmichel
 *
 */
public class ModelRef {
	/**
	 * Name as it can be found in <MODEL>
	 */
	public final String name;
	/**
	 * URI as it can be found in <MODEL>
	 */
	public final String uri;
	
	/**
	 * @param name
	 * @param url
	 */
	public ModelRef(String name, String uri) {
		super();
		this.name = name;
		this.uri = uri;
	}
	
	/**
	 * Return true if the instance name or uri matches name
	 * @param name
	 * @return
	 */
	public boolean matches(String name) {
		return ( this.name.equalsIgnoreCase(name) || this.uri.equalsIgnoreCase(name));
	}

}
