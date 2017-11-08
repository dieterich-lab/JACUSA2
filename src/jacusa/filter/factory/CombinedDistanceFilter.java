/**
 * 
 */
package jacusa.filter.factory;

import jacusa.filter.AbstractDistanceFilter;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;

/**
 * @author Michael Piechotta
 *
 */
public class CombinedDistanceFilter<T extends BaseQualData> 
extends AbstractDistanceFilter<T> {

	public CombinedDistanceFilter(final char c, 
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			final AbstractParameters<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);
	}

}
