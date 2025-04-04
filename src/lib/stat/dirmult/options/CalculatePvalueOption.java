package lib.stat.dirmult.options;

import org.apache.commons.cli.CommandLine;

import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.stat.dirmult.DirMultParameter;

public class CalculatePvalueOption extends AbstractProcessingOption {

	private final DirMultParameter dirMultParameter;
	
	public CalculatePvalueOption(
			final DirMultParameter dirMultParameter) {
		super("calcPvalue", "calcPvalue");
		
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		dirMultParameter.setCalcPValue(true);
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
