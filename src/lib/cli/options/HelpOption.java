package lib.cli.options;

import lib.cli.CLI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class HelpOption extends AbstractACOption {

	private CLI cli;
	
	public HelpOption(final CLI cli) {
		super("h", "help");
		this.cli = cli;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.hasArg(false)
				.desc("Print usage information")
				.build();
	}

	@Override
	public void process(CommandLine line) {
		if (line.hasOption(getOpt())) {
	    	cli.printUsage(); 
	    	System.exit(0);
	    }
	}

}