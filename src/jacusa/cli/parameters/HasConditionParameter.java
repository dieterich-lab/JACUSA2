package jacusa.cli.parameters;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;

/**
 * Interface to handle condition parameters.
 * 
 * @param <T>
 */
public interface HasConditionParameter<T extends AbstractData> {

	/**
	 * Returns the list of all condition parameter objects.
	 *  
	 * @return a list of AbstractConditionParameter objects
	 */
	List<AbstractConditionParameter<T>> getConditionParameters();
	
	/**
	 * Returns a specific conditionParameter object.
	 * 
	 * @param conditionIndex index identifies a specific condition
	 * @return a specific condition parameter object
	 */
	AbstractConditionParameter<T> getConditionParameter(int conditionIndex);
	
	/**
	 * Updates 
	 * 
	 * @param conditionParameters the new list of condition parameters
	 */
	void setConditionParameters(List<AbstractConditionParameter<T>> conditionParameters);

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
