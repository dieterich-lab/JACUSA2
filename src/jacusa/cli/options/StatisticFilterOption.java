package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameters;
import lib.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class StatisticFilterOption  extends AbstractACOption {

	private StatisticParameters<?> parameters;

	public StatisticFilterOption(StatisticParameters<?> parameters) {
		super("T", "threshold");
		this.parameters = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
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
	    	parameters.setThreshold(stat);
		}
	}

}