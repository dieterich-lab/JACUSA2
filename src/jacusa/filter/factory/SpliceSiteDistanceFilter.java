package jacusa.filter.factory;

import jacusa.filter.AbstractDistanceFilter;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;

/**
 * @author Michael Piechotta
 *
 */
public class SpliceSiteDistanceFilter<T extends BaseQualData> 
extends AbstractDistanceFilter<T> {

	public SpliceSiteDistanceFilter(final char c,
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			AbstractParameters<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);
	}

}
