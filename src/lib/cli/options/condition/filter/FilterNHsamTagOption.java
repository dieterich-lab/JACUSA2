package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.ConditionParameter;

public class FilterNHsamTagOption extends AbstractFilterSamTagConditionOption {

	public static final String TAG = "NH";
	
	public FilterNHsamTagOption(final ConditionParameter conditionParameter) {
		super(conditionParameter, TAG);
	}

	public FilterNHsamTagOption(final List<ConditionParameter> conditionParameters) {
		super(conditionParameters, TAG);
	}
	
	@Override
	protected MaxValueSamTagFilter createSamTagFilter(int value) {
		return new MaxValueSamTagFilter(TAG, value);
	}
	
}