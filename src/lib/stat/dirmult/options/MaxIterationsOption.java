package lib.stat.dirmult.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.estimate.MinkaParameter;

public class MaxIterationsOption extends AbstractProcessingOption {

	private final MinkaParameter minkaParameter;
	
	public MaxIterationsOption(final MinkaParameter minkaParameter) {
		super("maxIterations", "maxIterations");
		
		this.minkaParameter = minkaParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		final int matIterations = Integer.parseInt(cmd.getOptionValue(getLongOpt()));
		if (matIterations < 0) {
			throw new IllegalArgumentException(getLongOpt() + " must be > 0");
		}
		minkaParameter.setMaxIterations(matIterations);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt(getOpt())
				.hasArg(true)
				.desc("Maximum number of iterations for Newton's method.\nDefault: " + minkaParameter.getMaxIterations())
				.build();
	}

}
