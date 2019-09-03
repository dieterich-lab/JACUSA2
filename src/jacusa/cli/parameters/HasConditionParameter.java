package jacusa.cli.parameters;

import java.util.List;

import lib.cli.parameter.ConditionParameter;

/**
 * Interface to handle condition parameters.
 */
public interface HasConditionParameter {

	/**
	 * Returns the list of all condition parameter objects.
	 *  
	 * @return a list of AbstractConditionParameter objects
	 */
	List<ConditionParameter> getConditionParameters();
	
	/**
	 * Returns a specific conditionParameter object.
	 * 
	 * @param conditionIndex index identifies a specific condition
	 * @return a specific condition parameter object
	 */
	ConditionParameter getConditionParameter(int conditionIndex);
	
	/**
	 * Updates the list of condition parameters.
	 * 
	 * @param conditionParameters the new list of condition parameters
	 */
	void setConditionParameters(List<ConditionParameter> conditionParameters);

	/**
	 * Returns the current number of stored condition.
	 * 
	 * @return the number of stored conditions
	 */
	int getConditionsSize();
	
	/**
	 * Returns the number of replicate for a specific condition.
	 * 
	 * @param conditionIndex 
	 * @return the number of replicates for conditionIndex
	 */
	int getReplicates(int conditionIndex);

}
