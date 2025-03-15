package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MaxThreadOption extends AbstractOption {

	private final GeneralParameter parameter;
	
	public MaxThreadOption(GeneralParameter parameters) {
		super("p", "threads");
		this.parameter = parameters;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc("use # " + getLongOpt().toUpperCase() + " \n default: " + parameter.getMaxThreads())
		        .build();
	}

	/**
	 * Tested in @see test.lib.cli.options.MaxThreadOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
    	int maxThreads = Integer.parseInt(line.getOptionValue(getOpt()));
    	if(maxThreads < 1) {
    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0!");
    	}
    	parameter.setMaxThreads(maxThreads);
	}

}