package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.has.HasFilterMinRatio;

public class FilterMinRatioOption extends AbstractProcessingOption {

	private final HasFilterMinRatio hasFilterMinRatio;
	
	public FilterMinRatioOption(final HasFilterMinRatio hasFilterMinRatio) {
		super(null, "minRatio");
		this.hasFilterMinRatio = hasFilterMinRatio;
	}
	
	/**
	 * Tested in @see test.lib.cli.options.filter.FilterMinRatioOptionTest  
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final double tmpMinRatio = Double.parseDouble(line.getOptionValue(getLongOpt())); 
		if (tmpMinRatio < 0.0 || tmpMinRatio > 1.0) {
			throw new IllegalArgumentException(getLongOpt() + " needs to be within [0.0, 1.0]");
		}
		hasFilterMinRatio.setFilterMinRatio(tmpMinRatio);
	}
	
	@Override
	public Option getOption(boolean printExtendedHelp) {
		final double defaultMinRatio = hasFilterMinRatio.getFilterMinRatio();
		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Minimal ratio of base calls to pass filtering. Default: " + defaultMinRatio)
				.build();
	}
	
}
