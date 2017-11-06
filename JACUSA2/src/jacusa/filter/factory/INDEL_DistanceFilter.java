/**
 * 
 */
package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.AbstractDistanceFilter;

/**
 * @author Michael Piechotta
 *
 */
public class INDEL_DistanceFilter<T extends BaseQualData> 
extends	AbstractDistanceFilter<T> {

	public INDEL_DistanceFilter(final char c,
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			AbstractParameters<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);

	}
	
}
