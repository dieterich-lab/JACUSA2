package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class ThreadWindowSizeOption extends AbstractACOption {

	final private AbstractParameters<?> parameters; 
	
	public ThreadWindowSizeOption(AbstractParameters<?> parameters) {
		super("W", "thread-window-size");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName("THREAD-WINDOW-SIZE")
			.hasArg(true)
	        .withDescription("size of the window used per thread.\n default: " + parameters.getThreadReservedWindowSize())
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	int windowSize = Integer.parseInt(value);
	    	if (windowSize < 1) {
	    		throw new IllegalArgumentException("THREAD-WINDOW-SIZE too small: " + windowSize);
	    	}

	    	parameters.setThreadWindowSize(windowSize);
		}
	}

}
