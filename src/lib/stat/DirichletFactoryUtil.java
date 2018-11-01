package lib.stat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.io.format.call.VCFcallFormat;
import lib.io.ResultFormat;

public class DirichletFactoryUtil {

	private final ResultFormat resultFormat;
	private final DirichletParameter dirichletParameter;

	public DirichletFactoryUtil(final ResultFormat resultFormat) {
		this.resultFormat	= resultFormat;
		dirichletParameter 	= new DirichletParameter();
	}

	public Options getOptions() {
		final Options options = new Options();

		options.addOption(Option.builder()
				.longOpt("epsilon")
				.hasArg(true)
				.desc("Fit achieved if |L1 - L2| < epsilon, where L1 and L2 correspond to old and new likelihood respectively.\nDefault: " + dirichletParameter.getMinkaEstimateParameter().getEpsilon())
				.build());

		final int maxIterations = dirichletParameter.getMinkaEstimateParameter().getMaxIterations();
		options.addOption(Option.builder()
				.longOpt("maxIterations")
				.hasArg(true)
				.desc("Maximum number of iterations for Newton's method.\nDefault: " + maxIterations)
				.build());
		
		options.addOption(Option.builder()
				.longOpt("calculatePvalue")
				.hasArg(false)
				.desc("TODO")
				.build());
		
		options.addOption(Option.builder()
				.longOpt("showAlpha")
				.hasArg(false)
				.desc("Show detailed info of Newton's method in output (not in VCF output).")
				.build());
		
		return options;
	}
	
	public void processCLI(final CommandLine cmd) {
		// format: -u DirMult:epsilon=<epsilon>:maxIterations=<maxIterions>:onlyObserved

		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {

			case "epsilon":
				dirichletParameter.getMinkaEstimateParameter().setEpsilon(Double.parseDouble(cmd.getOptionValue(longOpt)));
				break;
				
			case "maxIterations":
				dirichletParameter.getMinkaEstimateParameter().setMaxIterations(Integer.parseInt(cmd.getOptionValue(longOpt)));
				break;
	
			case "calculatePvalue":
				dirichletParameter.setCalcPValue(true);
				break;
				
			case "showAlpha":
				if (resultFormat.getC() == VCFcallFormat.CHAR) {
					throw new IllegalStateException("VCF output format does not support showAlpha");
				}
				dirichletParameter.setShowAlpha(true);
				break;

			default:
				break;
			}
		}
	}
	
	public DirichletParameter getDirichletParameter() {
		return dirichletParameter;
	}
	
}
