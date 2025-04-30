package lib.cli.options;

import lib.cli.parameter.GeneralParameter;
import lib.io.InputOutput;
import lib.stat.dirmult.ProcessCommandLine;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowInsertionStartCountOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	private final ProcessCommandLine processingCommandLine;
	
	public ShowInsertionStartCountOption(
			final GeneralParameter parameter,
			final ProcessCommandLine processingCommandLine) {
		super("i", "show-start-insertions");
		this.parameter = parameter;
		this.processingCommandLine = processingCommandLine;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		final StringBuilder sb = new StringBuilder();
		if (printExtendedHelp) {
			sb.append(processingCommandLine.renderNestedOptions());
		} else {
			sb.append("...");
		}
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.optionalArg(true)
				.desc("Show insertion start score:\n" + sb.toString())
				.build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.FilterModusOptionTest
	 */
	
	@Override
	public void process(final CommandLine cmd) throws Exception {
		final String line = cmd.getOptionValue(getOpt());
		if (line != null) {
			final String[] args = line.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
			if (args.length > 0) {
				processingCommandLine.processNested(args);
			}
		
		}
		parameter.showInsertionStartsCount(true);
	}

}