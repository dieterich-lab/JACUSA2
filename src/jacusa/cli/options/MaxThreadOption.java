package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MaxThreadOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public MaxThreadOption(AbstractParameters<?> parameters) {
		super("p", "threads");
		this.parameters = parameters;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
				.withArgName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .withDescription("use # " + getLongOpt().toUpperCase() + " \n default: " + parameters.getMaxThreads())
		        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	int maxThreads = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(maxThreads < 1) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0!");
	    	}
	    	parameters.setMaxThreads(maxThreads);
	    }
	}

}