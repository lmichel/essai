package exceptions;

/**
 * Risen when an expected ressource is unconsistant
 * @author laurentmichel
 *
 */
public class UnconsistantResourceException extends MappingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnconsistantResourceException(String msg){
		super(msg);
	}

}
