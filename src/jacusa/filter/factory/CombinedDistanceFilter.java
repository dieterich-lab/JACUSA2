package jacusa.filter.factory;

import jacusa.filter.AbstractDistanceFilter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class CombinedDistanceFilter<T extends AbstractData & hasReferenceBase & hasBaseCallCount, F extends AbstractData & hasBaseCallCount> 
extends AbstractDistanceFilter<T, F> {

	public CombinedDistanceFilter(final char c, 
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			final AbstractFilterFactory<T, F> filterFactory) {

		super(c, filterDistance, filterMinRatio, filterMinCount, filterFactory);
	}

}
