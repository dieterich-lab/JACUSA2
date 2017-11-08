package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.filter.samtag.SamTagFilter;
import lib.cli.options.condition.filter.samtag.SamTagNMFilter;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

public class FilterNMsamTagOption<T extends AbstractData> extends FilterSamTagConditionOption<T> {
	
	private static final String TAG = "NM";
	
	public FilterNMsamTagOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(conditionIndex, conditionParameter, TAG);
	}

	public FilterNMsamTagOption(final List<AbstractConditionParameter<T>> conditionParameters) {
		super(conditionParameters, TAG);
	}
	
	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNMFilter(value);
	}

}
