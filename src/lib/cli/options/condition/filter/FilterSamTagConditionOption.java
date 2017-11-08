package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.options.condition.filter.samtag.SamTagFilter;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public abstract class FilterSamTagConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String LONG_OPT = "filter";
	private String tag;

	public FilterSamTagConditionOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter, final String tag) {
		super(new String(), LONG_OPT + tag, conditionIndex, conditionParameter);
		this.tag = tag;
	}

	public FilterSamTagConditionOption(final List<AbstractConditionParameter<T>> conditionParameters, final String tag) {
		super(new String(), LONG_OPT + tag, conditionParameters);
		this.tag = tag;
	}

	@Override
	public Option getOption() {
		String s = new String();

		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
		}
		// TODO
		s = "TODO Max " + tag + "-VALUE for SAM tag " + tag;

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(tag + "-VALUE")
				.hasArg(true)
		        .desc(s)
		        .build();
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getLongOpt())) {
	    	int value = Integer.parseInt(line.getOptionValue(getLongOpt()));
	    	for (final AbstractConditionParameter<T> conditionParameter : getConditionParameters()) {
	    		conditionParameter.getSamTagFilters().add(createSamTagFilter(value));
	    	}
	    }
	}

	protected abstract SamTagFilter createSamTagFilter(int value);  

}
