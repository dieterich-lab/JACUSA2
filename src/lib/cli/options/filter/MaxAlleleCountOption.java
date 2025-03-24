package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.has.HasMaxAlleleCount;
import lib.util.Base;

public class MaxAlleleCountOption extends AbstractProcessingOption {

	private final HasMaxAlleleCount hasMaxAlleleCount;
	
	public MaxAlleleCountOption(final HasMaxAlleleCount hasMaxAlleleCount) {
		super(null, "maxAlleles");
		this.hasMaxAlleleCount = hasMaxAlleleCount;
	}

	/**
	 * Tested in @set test.lib.cli.options.filter.MaxAlleleCountOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final int tmpCount = Integer.parseInt(line.getOptionValue(getLongOpt()));
		if (tmpCount < 1 || tmpCount > Base.validValues().length) {
			throw new IllegalArgumentException("Invalid allele count: " + getLongOpt());
		}
		hasMaxAlleleCount.setMaxAlleleCount(tmpCount);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		final int defaultCount = hasMaxAlleleCount.getMaxAlleleCount();
		return Option.builder()
				.hasArg()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.desc("must be > 0. Default: " + defaultCount)
				.build();
	}

}
