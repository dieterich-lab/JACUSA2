package jacusa.filter.factory.basecall.rtarrest;

import java.util.Set;

import org.apache.commons.cli.Option;

import jacusa.filter.factory.basecall.AbstractBCCfilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.options.filter.has.HasApply2reads;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;

/**
 * This abstract FilterFactory configures and helps to create base call count based filters for the 
 * rt-arrest method. It allows to chose on which base calls counting and filtering should be 
 * carried out, see Apply2readsBaseCallCountSwitch
 */
public abstract class AbstractRTarrestBaseCallcountFilterFactory 
extends AbstractBCCfilterFactory 
implements HasApply2reads {

	private final Apply2readsBaseCallCountSwitch bccSwitch;

	public AbstractRTarrestBaseCallcountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		this(
				option, 
				bccSwitch, filteredDataFetcher,
				AbstractBCCfilterFactory.DEFAULT_FILTER_DISTANCE,
				AbstractBCCfilterFactory.DEFAULT_FILTER_MINRATIO);
		
	}
	
	public AbstractRTarrestBaseCallcountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option, bccSwitch, filteredDataFetcher, defaultFilterDistance, defaultFilterMinRatio);
		this.bccSwitch = bccSwitch;
		// make search in arrest base calls for false positive variants the default
		getApply2Reads().add(RT_READS.ARREST);
		getApply2Reads().add(RT_READS.THROUGH);
		// TODO changing reads to filter is currenlty turn off
		// getACOption().add(new Apply2readsOption(this));
	}
	
	@Override
	public Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
}