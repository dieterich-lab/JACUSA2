package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class EnforceBASQOption extends AbstractACOption {

	private final GeneralParameter parameter;
	
	public EnforceBASQOption(GeneralParameter parameters) {
		super("p", "threads");
		this.parameter = parameters;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc("Enforce BASQ for all base calls. default: use existing BASQ")
		        .build();
	}

	/**
	 * Tested in @see test.lib.cli.options.MaxThreadOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
    	final byte enforceBASQ = (byte)Integer.parseInt(line.getOptionValue(getOpt()));
    	if(enforceBASQ < 1) {
    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be >= 0!");
    	}
    	parameter.enforceBASQ(enforceBASQ);
	}

}