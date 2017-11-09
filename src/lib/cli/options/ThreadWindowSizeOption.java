package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ThreadWindowSizeOption extends AbstractACOption {

	final private AbstractParameter<?> parameters; 
	
	public ThreadWindowSizeOption(AbstractParameter<?> parameters) {
		super("W", "thread-window-size");
		this.parameters = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("size of the window used per thread.\n default: " + parameters.getReservedWindowSize())
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	int windowSize = Integer.parseInt(value);
	    	if (windowSize < 1) {
	    		throw new IllegalArgumentException("THREAD-WINDOW-SIZE too small: " + windowSize);
	    	}

	    	parameters.setReservedWindowSize(windowSize);
		}
	}

}
