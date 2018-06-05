package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MinCoverageConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "c";
	private static final String LONG_OPT = "min-coverage";
	
	public MinCoverageConditionOption(final List<AbstractConditionParameter<T>> conditionParameter) {
		super(OPT, LONG_OPT, conditionParameter);
	}
	
	public MinCoverageConditionOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameters) {
		super(OPT, LONG_OPT, conditionIndex, conditionParameters);
	}
	
	@Override
	public Option getOption() {
		String s = new String();
		
		int minCoverage = getConditionParameter().getMinCoverage();
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
		}
		s = "filter positions with coverage < " + getLongOpt().toUpperCase() + 
				s + "\ndefault: " + minCoverage;

		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
			    .desc(s)
			    .build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
	    if(line.hasOption(getOpt())) {
	    	int minCoverage = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(minCoverage < 1) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0!");
	    	}
	    	
	    	for (final AbstractConditionParameter<T> condition : getConditionParameters()) {
	    		condition.setMinCoverage(minCoverage);
	    	}
	    }
	}

}
