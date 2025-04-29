package lib.stat.dirmult.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.stat.dirmult.EstimationParameter;

public class ShowAlphaOption extends AbstractProcessingOption {

	private final EstimationParameter dirMultParameter;
	
	public ShowAlphaOption(
			final EstimationParameter dirMultParameter) {
		super("showAlpha", "showAlpha");
		
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		dirMultParameter.setShowAlpha(true);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt("showAlpha")
				.hasArg(false)
				.desc("Show detailed info of Newton's method in output (not in VCF output).")
				.build();
	}

}
