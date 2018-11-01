package jacusa.filter.factory.distance.rtarrest;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

public abstract class AbstractRTarrestDistanceFilterFactory 
extends AbstractBaseCallCountFilterFactory {

	private final Apply2readsBaseCallCountSwitch bccSwitch;
	
	public AbstractRTarrestDistanceFilterFactory(
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
	public Set<Option> processCLI(final CommandLine cmd) throws MissingOptionException {
		final Set<Option> parsed = super.processCLI(cmd);
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
				case "reads":
					final String optionValue = cmd.getOptionValue(longOpt);
					if (optionValue.isEmpty()) {
						throw new MissingOptionException("Missing value for " + longOpt);
					}
					final Set<RT_READS> tmpApply2reads = RTarrestMethod.processApply2Reads(optionValue);
					if (tmpApply2reads.size() == 0) {
						throw new IllegalArgumentException("Unknown value for " + longOpt);
					}
					bccSwitch.getApply2reads().clear();
					bccSwitch.getApply2reads().addAll(tmpApply2reads);
					parsed.add(option);
					break;
			}
		}
		return parsed;
	}
	
	protected Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
}