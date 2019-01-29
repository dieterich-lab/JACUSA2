package jacusa.filter.factory.basecall.rtarrest;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

public abstract class AbstractRTarrestBaseCallcountFilterFactory 
extends AbstractBaseCallCountFilterFactory {

	private final Apply2readsBaseCallCountSwitch bccSwitch;
	
	public AbstractRTarrestBaseCallcountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option, bccSwitch, filteredDataFetcher, defaultFilterDistance, defaultFilterMinRatio);
		this.bccSwitch = bccSwitch;
	}

	@Override
	public Options getOptions() {
		return super.getOptions();
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) {
		final Set<Option> parsed = super.processCLI(cmd);
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
				case "reads":
					parseApply2reads(cmd, longOpt, bccSwitch);
					parsed.add(option);
					break;
			}
		}
		return parsed;
	}
	
	public static void parseApply2reads(
			final CommandLine cmd, 
			final String longOpt, 
			final Apply2readsBaseCallCountSwitch bccSwitch) {
		
		final String optionValue = cmd.getOptionValue(longOpt);
		final Set<RT_READS> tmpApply2reads = RTarrestMethod.processApply2Reads(optionValue);
		if (tmpApply2reads.size() == 0) {
			throw new IllegalArgumentException("Unknown value for " + longOpt);
		}
		bccSwitch.getApply2reads().clear();
		bccSwitch.getApply2reads().addAll(tmpApply2reads);
	}
	
	protected Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
}