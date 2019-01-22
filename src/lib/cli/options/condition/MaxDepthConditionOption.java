package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameter.ConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MaxDepthConditionOption extends AbstractConditionACOption {

	public static final int MIN_DEPTH = 1;
	public static final int UNLIMITED_DEPTH = -1;
	
	private static final String OPT = "d";
	private static final String LONG_OPT = "max-depth";
	
	public MaxDepthConditionOption(final ConditionParameter conditionParameter) {
		super(OPT, LONG_OPT, conditionParameter);
	}
	
	public MaxDepthConditionOption(final List<ConditionParameter> conditionParameters) {
		super(OPT, LONG_OPT, conditionParameters);
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String s = new String();

		int maxDepth = getConditionParameter().getMaxDepth();
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
		}
		s = "max read depth" + s + "\ndefault: " + maxDepth;
				
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.desc(s)
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	int maxDepth = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(maxDepth != UNLIMITED_DEPTH && maxDepth < MIN_DEPTH ) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0 or -1 (limited by memory)!");
	    	}
	    	
	    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
	    		conditionParameter.setMaxDepth(maxDepth);
	    	}
	    }
	}

}