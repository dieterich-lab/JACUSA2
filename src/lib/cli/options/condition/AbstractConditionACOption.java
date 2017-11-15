package lib.cli.options.condition;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.AbstractACOption;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

public abstract class AbstractConditionACOption<T extends AbstractData> 
extends AbstractACOption {

	private int conditionIndex;
	private final List<AbstractConditionParameter<T>> conditionParameters;
		
	public AbstractConditionACOption(final String opt, final String longOpt, final List<AbstractConditionParameter<T>> conditionParameters) {
		super(opt, longOpt);
		conditionIndex 	= -1;
		this.conditionParameters = conditionParameters;
	}
	
	public AbstractConditionACOption(final String opt, final String longOpt, final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(opt != null ? opt + (conditionIndex + 1) : null,
				longOpt != null ? longOpt + (conditionIndex + 1) : null);

		this.conditionIndex = conditionIndex;
		conditionParameters = new ArrayList<AbstractConditionParameter<T>>(1);
		conditionParameters.add(conditionParameter);
	}

	public List<AbstractConditionParameter<T>> getConditionParameters() {
		return conditionParameters;
	}
	
	public AbstractConditionParameter<T> getConditionParameter() {
		return conditionParameters.get(0);
	}

	public int getConditionIndex() {
		return conditionIndex == -1 ? -1 : conditionIndex + 1;
	}

}
