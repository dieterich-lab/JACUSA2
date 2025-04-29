package jacusa.cli.options;

import jacusa.cli.parameters.StatParameter;
import lib.cli.options.AbstractProcessingOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Enables the user to choose a threshold by command line
 * Currently, only the chosen threshold needs to be >= 0.
 */
public class ThresholdFilterOption  extends AbstractProcessingOption {
	
	public static final String OPT 		= "T";
	public static final String LONG_OPT = "threshold";
	
	private StatParameter statParameter;
	
	public ThresholdFilterOption(StatParameter statParameter) {
		super(OPT, LONG_OPT);
		this.statParameter = statParameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Filter positions based on test-statistic " +
						getLongOpt().toUpperCase() +
						"\n default: DO NOT FILTER")
				.build();
	}
	
	/**
	 * Tested @see test.jacusa.cli.options.StatFactoryOptionTest
	 */
	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
	    final String thresholdStr 	= line.getOptionValue(getOpt());
    	final double threshold 		= Double.parseDouble(thresholdStr);
    	if (threshold < 0) {
    		throw new IllegalArgumentException("Invalid value for " + getLongOpt().toUpperCase() + 
    				". Allowed values are " + getLongOpt().toUpperCase() + " >= 0");
    	}
    	statParameter.setThreshold(threshold);
	}

}
