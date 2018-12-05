package lib.cli.options;

import lib.cli.parameter.GeneralParameter;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterModusOption extends AbstractACOption {

	final private GeneralParameter parameter;
	
	public FilterModusOption(final GeneralParameter parameter) {
		super("s", "split");
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
	
	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.splitFiltered(true);
	}

}