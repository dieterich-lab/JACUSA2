package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasHomopolymerInfo;

public class HomopolymerDataFilter<T extends AbstractData & hasBaseCallCount & hasHomopolymerInfo> 
extends AbstractDataFilter<T> {

	public HomopolymerDataFilter(final char c, 
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {
		
		super(c, 0, parameter, conditionFilterCaches);
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		addFilteredData(parallelData);

		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);
		
		for (int i = 0; i < variantBaseIndexs.length; i++) {
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
					if (parallelData.getData(conditionIndex, replicateIndex).isHomopolymer()) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
}
