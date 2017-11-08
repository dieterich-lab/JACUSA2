package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MaxDepthConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "d";
	private static final String LONG_OPT = "max-depth";
	
	public MaxDepthConditionOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(OPT, LONG_OPT, conditionIndex, conditionParameter);
	}
	
	public MaxDepthConditionOption(final List<AbstractConditionParameter<T>> conditionParameters) {
		super(OPT, LONG_OPT, conditionParameters);
	}
	
	@Override
	public Option getOption() {
		String s = new String();

		int maxDepth = -1;
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
			maxDepth = getConditionParameters().get(getConditionIndex()).getMaxDepth();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
			maxDepth = getConditionParameters().get(0).getMaxDepth();
		}
		s = "max read depth" + s + "\ndefault: " + maxDepth;
				
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.desc(s)
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	int maxDepth = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(maxDepth < 2 || maxDepth == 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0 or -1 (limited by memory)!");
	    	}
	    	
	    	for (final AbstractConditionParameter<T> conditionParameter : getConditionParameters()) {
	    		conditionParameter.setMaxDepth(maxDepth);
	    	}
	    }
	}

}