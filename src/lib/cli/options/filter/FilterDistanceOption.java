package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.has.HasFilterDistance;

public class FilterDistanceOption extends AbstractACOption {

	private final HasFilterDistance hasFilterDistance;
	
	public FilterDistanceOption(final HasFilterDistance hasFilterDistance) {
		super(null, "distance");
		this.hasFilterDistance 		= hasFilterDistance;
	}

	/**
	 * Tested in @see test.lib.cli.options.filter.FilterDistanceOptionTest  
	 */
	@Override
	public void process(CommandLine cmd) throws Exception {
		final int tmpDistance = Integer.parseInt(cmd.getOptionValue(getLongOpt()));
		if (tmpDistance <= 0) {
			throw new IllegalArgumentException(getLongOpt() + " needs to be > 0");
		}
		hasFilterDistance.setFilterDistance(tmpDistance);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		final int defaultFilterDistance = hasFilterDistance.getFilterDistance();
		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Filter base calls within distance to feature. Default: " + defaultFilterDistance)
				.build();
	}

}
