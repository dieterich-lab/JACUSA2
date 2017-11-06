package jacusa.cli.options.condition.filter;

import java.util.List;

import jacusa.cli.options.condition.filter.samtag.SamTagFilter;
import jacusa.cli.options.condition.filter.samtag.SamTagNMFilter;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

public class FilterNMsamTagOption<T extends AbstractData> extends FilterSamTagConditionOption<T> {
	
	private static final String TAG = "NM";
	
	public FilterNMsamTagOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(conditionIndex, condition, TAG);
	}

	public FilterNMsamTagOption(final List<ConditionParameters<T>> conditions) {
		super(conditions, TAG);
	}
	
	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNMFilter(value);
	}

}
