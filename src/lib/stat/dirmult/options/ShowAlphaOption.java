package lib.stat.dirmult.options;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.parameter.GeneralParameter;
import lib.stat.dirmult.DirMultParameter;

public class ShowAlphaOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	private final DirMultParameter dirMultParameter;
	
	public ShowAlphaOption(
			final GeneralParameter parameter,
			final DirMultParameter dirMultParameter) {
		super("showAlpha", "showAlpha");
		
		this.parameter 			= parameter;
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		dirMultParameter.setShowAlpha(true);
		
		List<String> keys = Arrays.asList(
				"initAlpha",
				"alpha",
				"iteration",
				"logLikelihood");
		for (final String key : keys) {
			parameter.registerConditionKeys(key);
			parameter.registerKey(key + "P");
		}
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
