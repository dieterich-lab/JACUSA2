package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowDeletionCountOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	
	public ShowDeletionCountOption(final GeneralParameter parameter) {
		super("D", "show-deletions");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
		        .desc("Show deletion score")
		        .build();
	}
	
	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.showDeletionCount(true);
	}

}