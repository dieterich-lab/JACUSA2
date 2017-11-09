/**
 * 
 */
package jacusa.filter.factory;

import jacusa.filter.AbstractDistanceFilter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

/**
 * @author Michael Piechotta
 *
 */
public class INDEL_DistanceFilter<T extends PileupData> 
extends	AbstractDistanceFilter<T> {

	public INDEL_DistanceFilter(final char c,
			final int filterDistance, final double filterMinRatio, final int filterMinCount,
			AbstractParameter<T> parameters) {
		super(c, filterDistance, filterMinRatio, filterMinCount, parameters);

	}
	
}
