package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ThreadWindowSizeOption extends AbstractACOption {

	public final static int NO_WINDOWS= -1;
	
	final private AbstractParameter<?, ?> parameter; 
	
	public ThreadWindowSizeOption(AbstractParameter<?, ?> parameters) {
		super("W", "thread-window-size");
		this.parameter = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("size of the window used per thread.\n default: " + parameter.getReservedWindowSize())
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	int windowSize = Integer.parseInt(value);
	    	if (windowSize != NO_WINDOWS && windowSize < 100) { // TODO make check when variables set ensure windowSize << threadWindowSize
	    		throw new IllegalArgumentException("THREAD-WINDOW-SIZE too small: " + windowSize);
	    	}

	    	parameter.setReservedWindowSize(windowSize);
		}
	}

}
