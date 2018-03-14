package jacusa.cli.parameters;

import lib.data.AbstractData;

/**
 * Interface for Parameters that enables to use a statistic calculator.  
 */
public interface HasStatisticParameters<T extends AbstractData> {

	/**
	 * Returns 
	 * 
	 * @return statistic parameters
	 */
	StatisticParameter<T> getStatisticParameters();

}
