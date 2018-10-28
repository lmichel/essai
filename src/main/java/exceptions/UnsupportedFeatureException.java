package exceptions;

/**
 * Risen when an feature is not implemented yet
 * @author laurentmichel
 *
 */
public class UnsupportedFeatureException extends MappingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedFeatureException(String msg){
		super(msg);
	}

}
