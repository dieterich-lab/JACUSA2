package jacusa.filter;

import java.util.List;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.cache.record.RecordDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasBooleanFilterData;

/**
 * This class implements the homopolymorph filter that identifies variants
 * within regions of consecutive identical base calls as false positives. 
 * 
 * @param <T>
 */
public class HomopolymerDataFilter<T extends AbstractData & HasBaseCallCount & HasBooleanFilterData & HasReferenceBase> 
extends AbstractDataFilter<T> {

	public HomopolymerDataFilter(final char c, 
			final int overhang,  
			final AbstractParameter<T, ?> parameter,
			final List<List<RecordDataCache<T>>> conditionFilterCaches) {

		super(c, overhang, parameter, conditionFilterCaches);
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
				if (parallelData.getData(conditionIndex, replicateIndex).getBooleanFilterData().get(getC())) {
					return true;
				}
			}
		}

		return false;
	}
	
}
