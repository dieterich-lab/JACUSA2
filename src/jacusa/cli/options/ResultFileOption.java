package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.io.Output;
import jacusa.io.OutputWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ResultFileOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public ResultFileOption(AbstractParameters<?> parameters) {
		super("r", "result-file");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription("results are written to " + getLongOpt().toUpperCase() + " or STDOUT if empty")
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String resultPathname = line.getOptionValue(getOpt());
	    	Output output = new OutputWriter(resultPathname);
	    	parameters.setOutput(output);
	    }
	}

}