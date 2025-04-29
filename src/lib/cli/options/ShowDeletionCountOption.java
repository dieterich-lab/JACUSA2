package lib.cli.options;

import lib.cli.parameter.GeneralParameter;
import lib.io.InputOutput;
import lib.stat.dirmult.ProcessCommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;

public class ShowDeletionCountOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	private final ProcessCommandLine processingCommandLine;
	
	public ShowDeletionCountOption(
			final GeneralParameter parameter,
			final ProcessCommandLine processingCommandLine) {
		super("D", "show-deletions");
		this.parameter = parameter;
		this.processingCommandLine = processingCommandLine;
	}
	
	// TODO test
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		final StringBuilder sb = new StringBuilder();
		if (printExtendedHelp) {
			final HelpFormatter helpFormatter = new HelpFormatter();
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			helpFormatter.printOptions(pw, 200, processingCommandLine.getOptions(), 0, 1);
			final String s = sw.toString();
			sb.append(s.replaceAll("--", ""));
		} else {
			sb.append("...");
		}
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.optionalArg(true)
				.desc("Show deletion score:\n" + sb.toString())
				.build();
	}
	
	@Override
	public void process(final CommandLine cmd) throws Exception {
		final String line = cmd.getOptionValue(getOpt());
		// TODO where to store options
		final String[] args = line.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
		if (args.length > 0) {
			processingCommandLine.process(ProcessCommandLine.addDash(args));
		}
		parameter.showDeletionCount(true);
	}

}