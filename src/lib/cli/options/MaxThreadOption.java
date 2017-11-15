package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MaxThreadOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameter;
	
	public MaxThreadOption(AbstractParameter<?, ?> parameters) {
		super("p", "threads");
		this.parameter = parameters;
	}
	
	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc("use # " + getLongOpt().toUpperCase() + " \n default: " + parameter.getMaxThreads())
		        .build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	int maxThreads = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(maxThreads < 1) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0!");
	    	}
	    	parameter.setMaxThreads(maxThreads);
	    }
	}

}