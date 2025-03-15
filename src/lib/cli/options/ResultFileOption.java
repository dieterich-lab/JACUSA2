package lib.cli.options;

import java.nio.file.FileAlreadyExistsException;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * 
 */
public class ResultFileOption extends AbstractOption {

	private final GeneralParameter parameter;
	
	public ResultFileOption(GeneralParameter parameter) {
		super("r", "result-file");
		this.parameter = parameter;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
			.argName(getLongOpt().toUpperCase())
			.hasArg(true)
			.required()
	        .desc("results are written to " + getLongOpt().toUpperCase())
	        .build();
	}

	/**
	 * Tested in @see test.lib.cli.options.ResultFileOptionTest
	 */
	@Override
	public void process(final CommandLine line) throws FileAlreadyExistsException {
		final String resultFilename = line.getOptionValue(getOpt());
		parameter.setResultFilename(resultFilename);
	}

}