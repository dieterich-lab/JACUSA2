package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;

public class FilterNHsamTagOption<T extends AbstractData> extends FilterSamTagConditionOption<T> {

	private static final String TAG = "NH";
	
	public FilterNHsamTagOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(conditionIndex, conditionParameter, TAG);
	}

	public FilterNHsamTagOption(final List<AbstractConditionParameter<T>> conditionParameters) {
		super(conditionParameters, TAG);
	}
	
	@Override
	protected MaxValueSamTagFilter createSamTagFilter(int value) {
		return new MaxValueSamTagFilter(TAG, value);
	}

}