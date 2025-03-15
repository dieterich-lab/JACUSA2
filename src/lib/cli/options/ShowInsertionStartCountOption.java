package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowInsertionStartCountOption extends AbstractOption {

	private final GeneralParameter parameter;
	
	public ShowInsertionStartCountOption(final GeneralParameter parameter) {
		super("i", "show-start-insertions");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
		        .desc("Show start insertion score")
		        .build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.FilterModusOptionTest
	 */
	
	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.showInsertionStartsCount(true);
	}

}