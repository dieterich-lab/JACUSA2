package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.AbstractConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public abstract class AbstractFilterSamTagConditionOption extends AbstractConditionACOption {

	private static final String LONG_OPT = "filter";
	private String tag;

	public AbstractFilterSamTagConditionOption(final int conditionIndex, final AbstractConditionParameter conditionParameter, final String tag) {
		super(null, LONG_OPT + tag, conditionIndex, conditionParameter);
		this.tag = tag;
	}

	public AbstractFilterSamTagConditionOption(final List<AbstractConditionParameter> conditionParameters, final String tag) {
		super(null, LONG_OPT + tag, conditionParameters);
		this.tag = tag;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {

		String s = "Max " + tag + "-VALUE for SAM tag " + tag;
		if (getConditionIndex() >= 0) {
			s += " for condition " + getConditionIndex();
		} else {
			s += " for all conditions";
		}
		
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(tag + "-VALUE")
				.hasArg(true)
		        .desc(s)
		        .build();
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
    	int value = Integer.parseInt(line.getOptionValue(getLongOpt()));
    	if (value < 1) {
    		throw new IllegalArgumentException(getLongOpt() + " cannot be < 1");
    	}
    	for (final AbstractConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.getSamTagFilters().add(createSamTagFilter(value));
    	}
	}

	protected abstract MaxValueSamTagFilter createSamTagFilter(int value);  

}