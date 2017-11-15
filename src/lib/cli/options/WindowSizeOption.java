package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class WindowSizeOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameters; 
	
	public WindowSizeOption(AbstractParameter<?, ?> parameter) {
		super("w", "window-size");
		this.parameters = parameter;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("size of the window used for caching. Make sure this is greater than the read size \n default: " + 
	        		parameters.getActiveWindowSize())
	        	.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	int windowSize = Integer.parseInt(value);
	    	if (windowSize < 1) {
	    		throw new IllegalArgumentException("WINDOW-SIZE too small: " + windowSize);
	    	}

	    	parameters.setActiveWindowSize(windowSize);
		}
	}

}
