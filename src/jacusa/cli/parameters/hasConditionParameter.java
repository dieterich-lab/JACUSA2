package jacusa.cli.parameters;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;

/**
 * Interface to handle condition parameters.
 * @author Michael Piechotta
 * @param <T>
 */
public interface hasConditionParameter<T extends AbstractData> {

	/**
	 * Returns the list of all condition parameter objects. 
	 * @return a list of AbstractConditionParameter objects
	 */
	List<AbstractConditionParameter<T>> getConditionParameters();
	
	/**
	 * 
	 * @param conditionIndex index identifies a specific condition
	 * @return a specific condition parameter object
	 */
	AbstractConditionParameter<T> getConditionParameter(int conditionIndex);
	
	/**
	 * 
	 * @param conditionParameters
	 */
	void setConditionParameters(List<AbstractConditionParameter<T>> conditionParameters);

	int getConditionsSize();
	
	/**
	 * 
	 * @param conditionIndex 
	 * @return the number of replicates for conditionIndex
	 */
	int getReplicates(int conditionIndex);

}
