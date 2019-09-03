package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class WindowSizeOption extends AbstractACOption {

	private final GeneralParameter parameters; 
	
	public WindowSizeOption(GeneralParameter parameter) {
		super("w", "window-size");
		this.parameters = parameter;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("size of the window used for caching. Make sure this is greater than the read size \n default: " + 
	        		parameters.getActiveWindowSize())
	        	.build();
	}

	/**
	 * Tested in @see test.lib.cli.options.WindowSizeOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
    	String value = line.getOptionValue(getOpt());
    	int windowSize = Integer.parseInt(value);
    	if (windowSize < 1) {
    		throw new IllegalArgumentException("WINDOW-SIZE too small: " + windowSize);
    	}

    	parameters.setActiveWindowSize(windowSize);
	}

}
