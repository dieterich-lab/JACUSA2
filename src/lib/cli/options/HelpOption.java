package lib.cli.options;

import lib.cli.CLI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class HelpOption extends AbstractOption {

	private static final String OPT = "h";
	public static final String SHORT_MSG = "[...] Use -" + OPT + " to see extended help";
	
	private CLI cli;
	
	public HelpOption(final CLI cli) {
		super(OPT, "help");
		this.cli = cli;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
				.desc("Print extended usage information")
				.build();
	}

	/**
	 * Tested in @see lib.cli.options.HelpOptionTest
	 */
	@Override
	public void process(CommandLine line) {
    	cli.setPrintExtendedHelp();
	}

}