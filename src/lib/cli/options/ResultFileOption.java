package lib.cli.options;

import jacusa.io.Output;
import jacusa.io.OutputWriter;
import lib.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

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

	@Override
	public Option getOption() {
		return Option.builder().longOpt(getLongOpt())
			.argName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .desc("results are written to " + getLongOpt().toUpperCase() + " or STDOUT if empty")
	        .build();
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