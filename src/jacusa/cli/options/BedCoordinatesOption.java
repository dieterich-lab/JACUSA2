package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class BedCoordinatesOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public BedCoordinatesOption(AbstractParameters<?> parameters) {
		super("b", "bed");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase()) 
			.hasArg(true)
			.withDescription(getLongOpt().toUpperCase() + " file to scan for variants")
			.create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String pathname = line.getOptionValue(getOpt());
	    	File file = new File(pathname);
	    	if(!file.exists()) {
	    		throw new FileNotFoundException("BED file (" + pathname + ") in not accessible!");
	    	}
    		parameters.setBedPathname(pathname);
	    }
	}

}