package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterModusOption extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	
	public FilterModusOption(final GeneralParameter parameter) {
		super("s", "split");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.optionalArg(true)
				.argName("FILTERED-FILE")
		        .desc("Store feature-filtered results in another file (= RESULT-FILE" + 
		        		GeneralParameter.FILE_SUFFIX + " if no argument) or (= FILTERED-FILE)")
		        .build();
	}
	
	/**
	 * Tested in @see test.lib.cli.options.FilterModusOptionTest
	 */
	
	@Override
	public void process(final CommandLine line) throws Exception {
		// null if no arguments provided
		String filteredFilename = line.getOptionValue(getOpt());
		// fake empty argument
		if (filteredFilename == null) {
			filteredFilename = "";
		}
		parameter.setFilteredFilename(filteredFilename);
	}

}