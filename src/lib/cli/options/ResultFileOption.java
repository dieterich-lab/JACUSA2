package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ResultFileOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameter;
	
	public ResultFileOption(AbstractParameter<?, ?> parameter) {
		super("r", "result-file");
		this.parameter = parameter;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
			.longOpt(getLongOpt())
			.argName(getLongOpt().toUpperCase())
			.hasArg(true)
			.required()
	        .desc("results are written to " + getLongOpt().toUpperCase())
	        .build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	final String resultFilename = line.getOptionValue(getOpt());
	    	// TODO check overwriting file
	    	parameter.setResultFilename(resultFilename);
	    }
	}

}