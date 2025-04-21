package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class SeedOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	
	public SeedOption(GeneralParameter parameters) {
		super("S", "seed");
		this.parameter = parameters;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc("set seed for random number generator \n default: no seed")
		        .build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
    	parameter.setSeed(line.getOptionValue(getOpt()));
	}

}