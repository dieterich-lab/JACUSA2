package jacusa.cli.parameters;

/**
 * Interface for Parameters that enables to use a statistic calculator.  
 */
public interface HasStatParameter {

	/**
	 * Returns 
	 * 
	 * @return statistic parameters
	 */
	StatParameter getStatParameter();
	void setStatParameter(StatParameter statParameter);

}
