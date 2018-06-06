package jacusa.filter.basecall;

import java.util.Set;

import jacusa.filter.FilterRatio;
import lib.data.count.BaseCallCount;
import lib.util.Base;

public class BaseCallCountFilter {

	private final FilterRatio filterRatio;
	
	public BaseCallCountFilter(final FilterRatio filterRatio) {
		this.filterRatio = filterRatio;
	}

	public boolean filter(final Set<Base> variantBases, 
			final BaseCallCount[][] observed, final BaseCallCount[][] filtered) {

		for (final Base variantBase : variantBases) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < observed.length; ++conditionIndex) {
				final int replicates = observed[conditionIndex].length;
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					// observed count
					final BaseCallCount o = observed[conditionIndex][replicateIndex];
					final int tmpCount = o.getBaseCall(variantBase);
					count += tmpCount;
					// possible artefact count
					final BaseCallCount f = filtered[conditionIndex][replicateIndex];
					if (f != null) {
						filteredCount += tmpCount - f.getBaseCall(variantBase);						
					} else {
						filteredCount += tmpCount;
					}
				}
			}

			// check if too much filteredCount
			if (filterRatio.filter(count, filteredCount)) {
				return true;
			}
			
		}

		return false;

	}
	
}
