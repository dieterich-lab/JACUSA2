package lib.stat.dirmult.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.estimate.MinkaParameter;

public class EpsilonOptions extends AbstractProcessingOption {

	private final MinkaParameter minkaParameter;
	
	public EpsilonOptions(final MinkaParameter minkaParameter) {
		super("epsilon", "epsilon");
		
		this.minkaParameter = minkaParameter;
	}
	
	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt(getLongOpt())
				.hasArg(true)
				.desc("Fit achieved if |L1 - L2| < epsilon, where L1 and L2 correspond to old and " +  
						"new likelihood respectively.\nDefault: " + minkaParameter.getEpsilon())
				.build();
	}
	
	@Override
	public void process(CommandLine cmd) throws Exception {
		final double epsilon = Double.parseDouble(cmd.getOptionValue(getLongOpt()));
		if (epsilon <= 0) {
			throw new IllegalArgumentException(getLongOpt() + " must be > 0");
		}
		minkaParameter.setEpsilon(epsilon);
	}
	
}
