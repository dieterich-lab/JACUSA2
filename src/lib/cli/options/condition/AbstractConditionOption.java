package lib.cli.options.condition;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.parameter.ConditionParameter;

/**
 * Represents the CLI options that can be provided for each condition, e.g.: 
 * minimal BASQ, minimal coverage, etc. 
 */
public abstract class AbstractConditionOption extends AbstractProcessingOption {

	private int conditionIndex;
	private final List<ConditionParameter> conditionParameters;
		
	public AbstractConditionOption(final String opt, final String longOpt, final List<ConditionParameter> conditionParameters) {
		super(opt, longOpt);
		conditionIndex 				= -1;
		this.conditionParameters 	= conditionParameters;
	}
	
	public AbstractConditionOption(
			final String opt, final String longOpt, 
			final ConditionParameter conditionParameter) {
		super(opt != null ? opt + (conditionParameter.getConditionIndex() + 1) : null,
				longOpt != null ? longOpt + (conditionParameter.getConditionIndex() + 1) : null);

		this.conditionIndex = conditionParameter.getConditionIndex();
		conditionParameters = new ArrayList<>(1);
		conditionParameters.add(conditionParameter);
	}

	public List<ConditionParameter> getConditionParameters() {
		return conditionParameters;
	}
	
	public ConditionParameter getConditionParameter() {
		return conditionParameters.get(0); // FIXME - test
	}

	public int getConditionIndex() {
		return conditionIndex == -1 ? -1 : conditionIndex;
	}

}
