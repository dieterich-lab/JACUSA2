package jacusa.filter.factory.basecall.lrtarrest;

import org.apache.commons.cli.Option;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.filter.factory.basecall.rtarrest.AbstractRTarrestBaseCallcountFilterFactory;
import lib.cli.options.filter.has.HasApply2reads;
import lib.cli.options.filter.has.HasFilterDistance;
import lib.cli.options.filter.has.HasFilterMinRatio;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;

public abstract class AbstractLRTarrestBaseCallCountFilterFactory 
extends AbstractRTarrestBaseCallcountFilterFactory 
implements HasFilterDistance, HasFilterMinRatio, HasApply2reads {


	public AbstractLRTarrestBaseCallCountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				option, 
				bccSwitch, filteredDataFetcher, 
				AbstractBaseCallCountFilterFactory.DEFAULT_FILTER_DISTANCE,
				AbstractBaseCallCountFilterFactory.DEFAULT_FILTER_MINRATIO);
	}
	
	
}