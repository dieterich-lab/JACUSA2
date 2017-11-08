package jacusa.filter.factory;

import jacusa.filter.AbstractDistanceFilter;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;

public class ReadPositionDistanceFilter<T extends BaseQualData> 
extends AbstractDistanceFilter<T> {

	public ReadPositionDistanceFilter(final char c, 
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			final AbstractParameters<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);
	}
	
}
