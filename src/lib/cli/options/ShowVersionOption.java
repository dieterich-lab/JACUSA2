package lib.cli.options;

import lib.util.AbstractTool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowVersionOption extends AbstractProcessingOption {

	public ShowVersionOption() {
		super("v", "version");
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
		        .desc("show version")
		        .build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.ShowVersionOptionTest
	 */
	@Override
	public void process(final CommandLine line) throws Exception {
		final AbstractTool tool = AbstractTool.getLogger().getTool();
		final StringBuilder sb = new StringBuilder();
		sb.append(tool.getVersion());
		System.out.println(sb.toString());
	}
	
}