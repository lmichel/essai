package exceptions;

/**
 * Super classof all exception risen by a mapping issue
 * @author laurentmichel
 *
 */
public class MappingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MappingException(String msg){
		super(msg);
	}

}
