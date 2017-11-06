package jacusa.cli.options.condition.filter;

import java.util.List;

import jacusa.cli.options.condition.filter.samtag.SamTagFilter;
import jacusa.cli.options.condition.filter.samtag.SamTagNHFilter;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

public class FilterNHsamTagOption<T extends AbstractData> extends FilterSamTagConditionOption<T> {

	private static final String TAG = "NH";
	
	public FilterNHsamTagOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(conditionIndex, condition, TAG);
	}

	public FilterNHsamTagOption(final List<ConditionParameters<T>> conditions) {
		super(conditions, TAG);
	}
	
	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNHFilter(value);
	}

}