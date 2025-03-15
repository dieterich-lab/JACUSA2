package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowInsertionCountOption extends AbstractOption {

	private final GeneralParameter parameter;
	
	public ShowInsertionCountOption(final GeneralParameter parameter) {
		super("I", "show-insertions");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
		        .desc("Show insertion score")
		        .build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.FilterModusOptionTest
	 */
	
	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.showInsertionCount(true);
	}

}