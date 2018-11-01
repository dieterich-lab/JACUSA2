package lib.cli.options;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameter.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ReferenceFastaFilenameOption extends AbstractACOption {

	final private AbstractParameter parameters;
	
	public ReferenceFastaFilenameOption(AbstractParameter parameter) {
		super("R", "ref-fasta");
		this.parameters = parameter;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase()) 
				.hasArg(true)
				.desc(getLongOpt().toUpperCase() + " indexed reference FASTA")
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
    	final String filename = line.getOptionValue(getOpt());
    	final File file = new File(filename);
    	if(! file.exists()) {
    		throw new FileNotFoundException("Reference FASTA file (" + filename + ") in not accessible!");
    	}
		parameters.setReferernceFilename(filename);
	}

}