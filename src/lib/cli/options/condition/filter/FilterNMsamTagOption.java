package lib.cli.options.condition.filter;

import java.util.List;

import org.apache.commons.cli.CommandLine;

import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.ConditionParameter;

public class FilterNMsamTagOption extends AbstractFilterSamTagConditionOption {
	
	public static final String TAG = "NM";
	
	public FilterNMsamTagOption(final ConditionParameter conditionParameter) {
		super(conditionParameter, TAG);
	}

	public FilterNMsamTagOption(final List<ConditionParameter> conditionParameters) {
		super(conditionParameters, TAG);
	}

	@Override
	public void process(CommandLine line) throws Exception {
    	int value = Integer.parseInt(line.getOptionValue(getLongOpt()));
    	if (value < 0) {
    		throw new IllegalArgumentException(getLongOpt() + " cannot be < 0");
    	}
    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.getSamTagFilters().add(createSamTagFilter(value));
    	}
	}
	
	@Override
	protected MaxValueSamTagFilter createSamTagFilter(int value) {
		return new MaxValueSamTagFilter(TAG, value);
	}

}
