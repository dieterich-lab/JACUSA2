package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameter;
import lib.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Enables the user to choose a threshold by command line
 * @author Michael Piechotta
 */
public class StatisticFilterOption  extends AbstractACOption {

	private StatisticParameter<?> statisticParamter;

	public StatisticFilterOption(StatisticParameter<?> parameters) {
		super("T", "threshold");
		this.statisticParamter = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Filter positions based on test-statistic " + getLongOpt().toUpperCase() + "\n default: DO NOT FILTER")
				.build();
	}

	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
		if (line.hasOption(getOpt())) {
		    final String value = line.getOptionValue(getOpt());
	    	final double stat = Double.parseDouble(value);
	    	if (stat < 0) {
	    		throw new IllegalArgumentException("Invalid value for " + getLongOpt().toUpperCase() + 
	    				". Allowed values are 0 <= " + getLongOpt().toUpperCase());
	    	}
	    	statisticParamter.setThreshold(stat);
		}
	}

}