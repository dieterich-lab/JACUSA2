package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.AbstractDistanceFilter;

public class ReadPositionDistanceFilter<T extends BaseQualData> 
extends AbstractDistanceFilter<T> {

	public ReadPositionDistanceFilter(final char c, 
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			final AbstractParameters<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);
	}
	
}
