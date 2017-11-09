package jacusa.filter.factory;

import jacusa.filter.AbstractDistanceFilter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

public class ReadPositionDistanceFilter<T extends PileupData> 
extends AbstractDistanceFilter<T> {

	public ReadPositionDistanceFilter(final char c, 
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			final AbstractParameter<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);
	}
	
}
