package lib.cli.options;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;

import lib.cli.parameter.AbstractParameter;

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
	public void process(CommandLine line) throws FileAlreadyExistsException {
		if (line.hasOption(getOpt())) {
	    	final String resultFilename = line.getOptionValue(getOpt());
	    	final File file = new File(resultFilename);
		 	if (file.exists()) {
		 		throw new FileAlreadyExistsException(resultFilename);
		 	}
	    	parameter.setResultFilename(resultFilename);
	    }
	}

}