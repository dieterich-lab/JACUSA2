package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.AbstractConditionOption;
import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.ConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

abstract class AbstractFilterSamTagConditionOption extends AbstractConditionOption {

	private static final String LONG_OPT = "filter";
	private String tag;

	AbstractFilterSamTagConditionOption(final ConditionParameter conditionParameter, final String tag) {
		super(LONG_OPT + tag + "_", LONG_OPT + tag + "_", conditionParameter);
		this.tag = tag;
	}

	AbstractFilterSamTagConditionOption(final List<ConditionParameter> conditionParameters, final String tag) {
		super(LONG_OPT + tag, LONG_OPT + tag, conditionParameters);
		this.tag = tag;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {

		String s = "Max " + tag + "-VALUE for SAM tag " + tag;
		if (getConditionIndex() >= 0) {
			s += " for condition " + getConditionIndex() + 1;
		} else {
			s += " for all conditions";
		}
		
		return Option.builder(getOpt())
				.argName(tag)
				.hasArg(true)
		        .desc(s)
		        .build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.condition.filter.FilterNHsamTagConditionOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
    	int value = Integer.parseInt(line.getOptionValue(getOpt()));
    	if (value < 1) {
    		throw new IllegalArgumentException(getOpt() + " cannot be < 1");
    	}
    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.getSamTagFilters().add(createSamTagFilter(value));
    	}
	}

	protected abstract MaxValueSamTagFilter createSamTagFilter(int value);  

}
