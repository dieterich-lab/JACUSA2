package lib.cli.options;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameter.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class BedCoordinatesOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameters;
	
	public BedCoordinatesOption(AbstractParameter<?, ?> parameters) {
		super("b", "bed");
		this.parameters = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase()) 
				.hasArg(true)
				.desc(getLongOpt().toUpperCase() + " file to scan for variants")
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String pathname = line.getOptionValue(getOpt());
	    	File file = new File(pathname);
	    	if(!file.exists()) {
	    		throw new FileNotFoundException("BED file (" + pathname + ") in not accessible!");
	    	}
    		parameters.setInputBedFilename(pathname);
	    }
	}

}