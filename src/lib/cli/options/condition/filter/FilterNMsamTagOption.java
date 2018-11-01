package lib.cli.options.condition.filter;

import java.util.List;

import org.apache.commons.cli.CommandLine;

import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.AbstractConditionParameter;

public class FilterNMsamTagOption extends AbstractFilterSamTagConditionOption {
	
	public static final String TAG = "NM";
	
	public FilterNMsamTagOption(final int conditionIndex, final AbstractConditionParameter conditionParameter) {
		super(conditionIndex, conditionParameter, TAG);
	}

	public FilterNMsamTagOption(final List<AbstractConditionParameter> conditionParameters) {
		super(conditionParameters, TAG);
	}

	@Override
	public void process(CommandLine line) throws Exception {
    	int value = Integer.parseInt(line.getOptionValue(getLongOpt()));
    	if (value < 0) {
    		throw new IllegalArgumentException(getLongOpt() + " cannot be < 0");
    	}
    	for (final AbstractConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.getSamTagFilters().add(createSamTagFilter(value));
    	}
	}
	
	@Override
	protected MaxValueSamTagFilter createSamTagFilter(int value) {
		return new MaxValueSamTagFilter(TAG, value);
	}

}
