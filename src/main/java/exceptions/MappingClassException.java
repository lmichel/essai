package exceptions;

/**
 * Risen when an unimplemented method is called
 * @author laurentmichel
 *
 */
public class MappingClassException extends MappingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MappingClassException(String msg){
		super(msg);
	}

}
