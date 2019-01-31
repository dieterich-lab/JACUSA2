package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.ConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterFlagConditionOption extends AbstractConditionACOption {

	// obsolete private static final String OPT = "F";
	private static final String LONG_OPT = "filterFlags";
	
	public FilterFlagConditionOption(final List<ConditionParameter> conditionParameters) {
		super(LONG_OPT, LONG_OPT, conditionParameters);
	}

	public FilterFlagConditionOption(ConditionParameter conditionParameter) {
		super(LONG_OPT, LONG_OPT, conditionParameter);
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String s = new String();
		
		int filterFlags = getConditionParameter().getFilterFlags();
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
		}
		s = "filter reads with flags " + getLongOpt().toUpperCase() + 
				s + "\ndefault: " + filterFlags;

		return Option.builder(getLongOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc(s)
		        .build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
    	final String value = line.getOptionValue(getOpt());
    	final int filterFlags = Integer.parseInt(value);
    	if (filterFlags <= 0) {
    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + filterFlags + " not valid.");
    	}

    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.setFilterFlags(filterFlags);
    	}
	}

}