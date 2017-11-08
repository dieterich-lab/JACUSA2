package jacusa.cli.parameters;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

public interface hasDefaultConditions<T extends AbstractData> {

	List<AbstractConditionParameter<T>> getConditionParameters();
	AbstractConditionParameter<T> getConditionParameters(int conditionIndex);
	void setConditionParameters(final List<AbstractConditionParameter<T>> conditionParameters);
	
	int getConditionsSize();
	int getReplicates(int conditionIndex);

}
