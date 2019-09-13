package jacusa.cli.parameters;

/**
 * Interface for Parameters that enables to use a statistic calculator.
 */
public interface HasStatParameter {

	/**
	 * Returns a StatParameter object.
	 * 
	 * @return stat parameter object.
	 * 
	 * Tested in @see test.jacusa.cli.parameters.HasStatParameterTest
	 */
	StatParameter getStatParameter();
	
	
	/**
	 * Updates the StatParameter object.
	 * 
	 * @param statParameter object to be used.
	 */
	void setStatParameter(StatParameter statParameter);

}
