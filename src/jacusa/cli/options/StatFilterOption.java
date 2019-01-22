package jacusa.cli.options;

import jacusa.cli.parameters.StatParameter;
import lib.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Enables the user to choose a threshold by command line
 * @author Michael Piechotta
 */
public class StatFilterOption  extends AbstractACOption {

	public static final String OPT = "T";
	public static final String LONG_OPT = "threshold";
	
	private StatParameter statisticParamter;

	public StatFilterOption(StatParameter parameters) {
		super(OPT, LONG_OPT);
		this.statisticParamter = parameters;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Filter positions based on test-statistic " + getLongOpt().toUpperCase() + "\n default: DO NOT FILTER")
				.build();
	}

	/**
	 * Tested @see test.jacusa.cli.options.StatFactoryOptionTest
	 */
	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
	    final String value = line.getOptionValue(getOpt());
    	final double stat = Double.parseDouble(value);
    	if (stat < 0) {
    		throw new IllegalArgumentException("Invalid value for " + getLongOpt().toUpperCase() + 
    				". Allowed values are " + getLongOpt().toUpperCase() + " >= 0");
    	}
    	statisticParamter.setThreshold(stat);
	}

}
