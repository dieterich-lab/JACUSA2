package lib.stat.betabin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.estimate.MinkaParameter;
import lib.stat.dirmult.DirMultCLIprocessing;

public class LRTarrestBetaBinCLIProcessing implements DirMultCLIprocessing {

	private final LRTarrestBetaBinParameter dirMultParameter;

	public LRTarrestBetaBinCLIProcessing(final LRTarrestBetaBinParameter dirMultParameter) {
		this.dirMultParameter 	= dirMultParameter;
	}
	
	@Override
	public Options getOptions() {
		final Options options = new Options();

		final MinkaParameter minkaParameter = dirMultParameter.getMinkaEstimateParameter();
		
		options.addOption(Option.builder()
				.longOpt("epsilon")
				.hasArg(true)
				.desc("Fit achieved if |L1 - L2| < epsilon, where L1 and L2 correspond to old and " +  
						"new likelihood respectively.\nDefault: " + minkaParameter.getEpsilon())
				.build());

		final int maxIterations = minkaParameter.getMaxIterations();
		options.addOption(Option.builder()
				.longOpt("maxIterations")
				.hasArg(true)
				.desc("Maximum number of iterations for Newton's method.\nDefault: " + maxIterations)
				.build());
		
		options.addOption(Option.builder()
				.longOpt("showAlpha")
				.hasArg(false)
				.desc("Show detailed info of Newton's method in output (not in VCF output).")
				.build());
		
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		final MinkaParameter minkaParameter = dirMultParameter.getMinkaEstimateParameter();
		
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {

			case "epsilon":
				minkaParameter.setEpsilon(Double.parseDouble(cmd.getOptionValue(longOpt)));
				break;
				
			case "maxIterations":
				minkaParameter.setMaxIterations(Integer.parseInt(cmd.getOptionValue(longOpt)));
				break;
	
			case "showAlpha":
				dirMultParameter.setShowAlpha(true);
				break;

			default:
				break;
			}
		}
	}
	
}
