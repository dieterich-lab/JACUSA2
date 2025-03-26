package lib.stat.dirmult.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.parameter.GeneralParameter;
import lib.stat.dirmult.DirMultParameter;

public class CalculatePvalueOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	private final DirMultParameter dirMultParameter;
	
	// TODO hide for JACUSA2
	public CalculatePvalueOption(
			final GeneralParameter parameter,
			final DirMultParameter dirMultParameter) {
		super("calcPvalue", "calcPvalue");
		
		this.parameter			= parameter;
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		dirMultParameter.setCalcPValue(true);
		parameter.registerKey("pvalue"); // FIXME what is the key
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt("calcPvalue")
				.hasArg(false)
				.desc("Calculate a pvalue based on a chi^2 approximation of the likelihood ratio")
				.build();
	}

}
