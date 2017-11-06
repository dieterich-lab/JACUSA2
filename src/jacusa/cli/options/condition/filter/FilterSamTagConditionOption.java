package jacusa.cli.options.condition.filter;

import java.util.List;

import jacusa.cli.options.condition.AbstractConditionACOption;
import jacusa.cli.options.condition.filter.samtag.SamTagFilter;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public abstract class FilterSamTagConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String LONG_OPT = "filter";
	private String tag;

	public FilterSamTagConditionOption(final int conditionIndex, final ConditionParameters<T> condition, final String tag) {
		super(new String(), LONG_OPT + tag, conditionIndex, condition);
		this.tag = tag;
	}

	public FilterSamTagConditionOption(final List<ConditionParameters<T>> conditions, final String tag) {
		super(new String(), LONG_OPT + tag, conditions);
		this.tag = tag;
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getLongOpt())) {
	    	int value = Integer.parseInt(line.getOptionValue(getLongOpt()));
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.getSamTagFilters().add(createSamTagFilter(value));
	    	}
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();

		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditions().size() > 1) {
			s = " for all conditions";
		}
		s = "Max " + tag + "-VALUE for SAM tag " + s;

		return OptionBuilder.withLongOpt(getLongOpt())
				.withArgName(tag + "-VALUE")
				.hasArg(true)
		        .withDescription(s)
		        .create();
	}

	protected abstract SamTagFilter createSamTagFilter(int value);  

}
