package lib.cli.options.condition;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.AbstractACOption;
import lib.cli.parameter.ConditionParameter;

/**
 * Represents the CLI options that can be provided for each condition, e.g.: 
 * minimal BASQ, minimal coverage, etc. 
 */
public abstract class AbstractConditionACOption extends AbstractACOption {

	private int condI;
	private final List<ConditionParameter> conditionParameters;
		
	public AbstractConditionACOption(final String opt, final String longOpt, final List<ConditionParameter> conditionParameters) {
		super(opt, longOpt);
		condI 				= -1;
		this.conditionParameters 	= conditionParameters;
	}
	
	public AbstractConditionACOption(
			final String opt, final String longOpt, 
			final ConditionParameter conditionParameter) {
		super(opt != null ? opt + (conditionParameter.getcondI()) : null,
				longOpt != null ? longOpt + (conditionParameter.getcondI()) : null);

		this.condI = conditionParameter.getcondI();
		conditionParameters = new ArrayList<>(1);
		conditionParameters.add(conditionParameter);
	}

	public List<ConditionParameter> getConditionParameters() {
		return conditionParameters;
	}
	
	public ConditionParameter getConditionParameter() {
		return conditionParameters.get(0);
	}

	public int getcondI() {
		return condI == -1 ? -1 : condI;
	}

}
