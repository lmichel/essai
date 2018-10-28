/**
 * 
 */
package mapping.types;

/**
 * {@link Type} container for numerical values (double actually)
 * @author michel
 *
 * @author michel
 *
 */
public class Numerical extends Type{
	/**
	 * content is a simple double
	 */
	private double content;

	/**
	 * Store a String as a double
	 * If the conversion cannot be done the value is set as NaN
	 * @param content
	 */
	public Numerical(final String content)  {
		super();
		try {
			this.content = Double.parseDouble(content);
		} catch (Exception e) {
			this.content = Double.NaN;
		}
	}
	
	/**
	 * @param Content
	 */
	public Numerical(final double Content) {
		super();
		this.content = Content;
	}
	
	/**
	 * Store a long as a double
	 * @param Content
	 */
	public Numerical(final long Content) {
		super();
		this.content = (double)Content;
	}


	/* (non-Javadoc)
	 * @see mapping.types.Type#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return true;
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getStringValue()
	 */
	@Override
	public String getStringValue() {
		return Double.toString(this.content);
	}

	/* (non-Javadoc)
	 * @see mapping.types.Type#getNumericalValue()
	 */
	@Override
	public double getNumericalValue() {
		return this.content;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.getStringValue();
	}

}
