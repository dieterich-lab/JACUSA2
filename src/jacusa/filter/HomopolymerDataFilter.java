package jacusa.filter;

import java.util.List;

import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasHomopolymerInfo;
import lib.data.has.HasReferenceBase;

/**
 * This class implements the homopolymorph filter that identifies variants
 * within regions of consecutive identical base calls as false positives. 
 * 
 * @param <T>
 */
public class HomopolymerDataFilter<T extends AbstractData & HasBaseCallCount & HasHomopolymerInfo & HasReferenceBase> 
extends AbstractDataFilter<T> {

	public HomopolymerDataFilter(final char c, 
			final int overhang,  
			final AbstractParameter<T, ?> parameter,
			final List<List<FilterCache<T>>> conditionFilterCaches) {

		super(c, overhang, parameter, conditionFilterCaches);
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		// get variants bases
		final int[] variantBaseIndexs = ParallelData.getVariantBaseIndexs(parallelData);

		// try to identify variants base calls within homopolymers
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
