package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.io.format.call.VCFcallFormat;
import lib.estimate.MinkaParameter;
import lib.io.ResultFormat;

public class CallDirMultCLIProcessing implements DirMultCLIprocessing {

	private final ResultFormat resultFormat;
	private final DirMultParameter dirMultParameter;

	public CallDirMultCLIProcessing(final ResultFormat resultFormat, final DirMultParameter dirMultParameter) {
		this.resultFormat		= resultFormat;
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
		
		/* TODO decide if to provide this option
		options.addOption(Option.builder()
				.longOpt("calculatePvalue")
				.hasArg(false)
				.desc("TODO")
				.build());
		 */
		
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
	
			case "calculatePvalue":
				dirMultParameter.setCalcPValue(true);
				break;
				
			case "showAlpha":
				if (resultFormat.getC() == VCFcallFormat.CHAR) {
					throw new IllegalStateException("VCF output format does not support showAlpha");
				}
				dirMultParameter.setShowAlpha(true);
				break;

			default:
				break;
			}
		}
	}
	
}
