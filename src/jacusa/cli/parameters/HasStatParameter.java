package jacusa.cli.parameters;

/**
 * Interface for Parameters that enables to use a statistic calculator.  
 */
public interface HasStatParameter {

	/**
	 * Returns 
	 * 
	 * @return statistic parameters
	 * 
	 * Tested in @see test.jacusa.cli.parameters.HasStatParameterTest
	 */
	StatParameter getStatParameter();
	void setStatParameter(StatParameter statParameter);

}
