package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class StatisticFilterOption  extends AbstractACOption {

	private StatisticParameters<?> parameters;

	public StatisticFilterOption(StatisticParameters<?> parameters) {
		super("T", "threshold");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription("Filter positions based on test-statistic " + getLongOpt().toUpperCase() + "\n default: DO NOT FILTER")
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
		    String value = line.getOptionValue(getOpt());
	    	double stat = Double.parseDouble(value);
	    	if (stat < 0) {
	    		throw new Exception("Invalid value for " + getLongOpt().toUpperCase() + 
	    				". Allowed values are 0 <= " + getLongOpt().toUpperCase());
	    	}
	    	parameters.setThreshold(stat);
		}
	}

}