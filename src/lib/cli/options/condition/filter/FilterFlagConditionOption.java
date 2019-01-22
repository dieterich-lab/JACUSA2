package lib.cli.options.condition.filter;

import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.ConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterFlagConditionOption extends AbstractConditionACOption {

	private static final String OPT = "F";
	private static final String LONG_OPT = "filter-flags";
	
	public FilterFlagConditionOption(final List<ConditionParameter> conditionParameter) {
		super(OPT, LONG_OPT, conditionParameter);
	}

	public FilterFlagConditionOption(ConditionParameter conditionParameter) {
		super(OPT, LONG_OPT, conditionParameter);
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

		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc(s)
		        .build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
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

}