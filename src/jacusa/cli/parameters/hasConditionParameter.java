package jacusa.cli.parameters;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;

public interface hasConditionParameter<T extends AbstractData> {

	List<AbstractConditionParameter<T>> getConditionParameters();
	AbstractConditionParameter<T> getConditionParameter(int conditionIndex);
	void setConditionParameters(final List<AbstractConditionParameter<T>> conditionParameters);

	int getConditionsSize();
	int getReplicates(int conditionIndex);

}
