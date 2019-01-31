package jacusa.filter.factory.basecall.rtarrest;

import java.util.Set;

import org.apache.commons.cli.Option;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.options.filter.Apply2readsOption;
import lib.cli.options.filter.has.HasApply2reads;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;

public abstract class AbstractRTarrestBaseCallcountFilterFactory 
extends AbstractBaseCallCountFilterFactory 
implements HasApply2reads {

	private final Apply2readsBaseCallCountSwitch bccSwitch;

	public AbstractRTarrestBaseCallcountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		this(
				option, 
				bccSwitch, filteredDataFetcher,
				AbstractBaseCallCountFilterFactory.DEFAULT_FILTER_DISTANCE,
				AbstractBaseCallCountFilterFactory.DEFAULT_FILTER_MINRATIO);
	}
	
	public AbstractRTarrestBaseCallcountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option, bccSwitch, filteredDataFetcher, defaultFilterDistance, defaultFilterMinRatio);
		this.bccSwitch = bccSwitch;
		getACOption().add(new Apply2readsOption(this));
	}
	
	@Override
	public Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
}