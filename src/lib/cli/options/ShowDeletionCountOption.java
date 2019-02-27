package lib.cli.options;

import lib.cli.parameter.GeneralParameter;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowDeletionCountOption extends AbstractACOption {

	final private GeneralParameter parameter;
	
	public ShowDeletionCountOption(final GeneralParameter parameter) {
		super("D", "show-deletions");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
		        .desc("Store feature-filtered results in another file (= RESULT-FILE" + 
		        		WorkerDispatcher.FILE_SUFFIX + ")")
		        .build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.FilterModusOptionTest
	 */
	
	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.showDeletionCount(true);
	}

}