package jacusa.cli.options.condition;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinCoverageConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "c";
	private static final String LONG_OPT = "min-coverage";
	
	public MinCoverageConditionOption(final List<ConditionParameters<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	public MinCoverageConditionOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();
		
		ConditionParameters<T> template = new ConditionParameters<T>(null);
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditions().size() > 1) {
			s = " for all conditions";
		}
		s = "filter positions with coverage < " + getLongOpt().toUpperCase() + 
				s + "\ndefault: " + template.getMinCoverage();

		return OptionBuilder.withLongOpt(getLongOpt())
					.withArgName(getLongOpt().toUpperCase())
					.hasArg(true)
			        .withDescription(s)
			        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
	    if(line.hasOption(getOpt())) {
	    	int minCoverage = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(minCoverage < 1) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0!");
	    	}
	    	
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.setMinCoverage(minCoverage);
	    	}
	    }
	}

}
