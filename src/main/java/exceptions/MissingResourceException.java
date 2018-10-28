package exceptions;

/**
 * Risen when an expected ressource is missing
 * @author laurentmichel
 *
 */
public class MissingResourceException extends MappingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingResourceException(String msg){
		super(msg);
	}

}
