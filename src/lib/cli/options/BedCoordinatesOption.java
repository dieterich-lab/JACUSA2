package lib.cli.options;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * CLI option to set input BED file that enables coordinate specific BAM traversal.
 */
public class BedCoordinatesOption extends AbstractProcessingOption {

	private final GeneralParameter parameters;
	
	public BedCoordinatesOption(GeneralParameter parameters) {
		super("b", "bed");
		this.parameters = parameters;
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase()) 
				.hasArg(true)
				.desc(getLongOpt().toUpperCase() + " file to scan for variants")
				.build();
	}

	/**
	 * Tested in @see test.lib.cli.options.BedCoordinatesOptionTest
	 */
	@Override
	public void process(final CommandLine line) throws FileNotFoundException {
    	final String pathname = line.getOptionValue(getOpt());
    	final File file = new File(pathname);
    	if(! file.exists()) {
    		throw new FileNotFoundException("BED file (" + pathname + ") in not accessible!");
    	}
		parameters.setInputBedFilename(pathname);
	}

}