package jacusa.filter.basecall;

import java.util.List;
import java.util.Set;


import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterByRatio;
import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

/**
 * Abstract class that enables filtering based on base call count data and some other filter chached data.
 * 
 * @param 
 */
public class GenericBaseCallCountFilter
extends AbstractFilter {
	
	private final Fetcher<BaseCallCount> observedBccFetcher;
	private final Fetcher<BaseCallCount> filteredBccFetcher;

	private final FilterByRatio filterByRatio;
	
	public GenericBaseCallCountFilter(final char c, 
			final Fetcher<BaseCallCount> observedBccFetcher,
			final Fetcher<BaseCallCount> filteredBccFetcher,
			final int overhang, 
			final FilterByRatio filterByRatio) {

		super(c, overhang);
		this.observedBccFetcher 	= observedBccFetcher;
		this.filteredBccFetcher 	= filteredBccFetcher;

		this.filterByRatio = filterByRatio;
	}

	@Override
	protected boolean filter(final ParallelData parallelData) {
		Set<Base> variantBases = null;
		if (parallelData.getConditions() == 1) {
			variantBases = ParallelData.getNonReferenceBases(
					parallelData.getCoordinate(), 
					parallelData.getLibraryType(),
					parallelData.getCombinedPooledData().getReferenceBase() );
		} else {
			final BaseCallCount bcc = observedBccFetcher.fetch(parallelData.getCombinedPooledData());
			final Set<Base> alleles = bcc.getAlleles();
			final List<BaseCallCount> bccs = Fetcher.apply(observedBccFetcher, parallelData.getCombinedData());
			variantBases = ParallelData.getVariantBases(alleles, bccs);
		}

		return filter(variantBases, parallelData);
	}

	private boolean filter(
			final Set<Base> variantBases, 
			final ParallelData parallelData) {

		for (final Base variantBase : variantBases) {
			int count = 0;
			int filteredCount = 0;

			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				final int replicates = parallelData.getReplicates(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
					final DataTypeContainer container = parallelData.getDataContainer(conditionIndex, replicateIndex);
					// observed count
					final BaseCallCount o = observedBccFetcher.fetch(container);
					final int tmpCount = o.getBaseCall(variantBase);
					count += tmpCount;
					// artefacts
					final BaseCallCount filteredBcc = filteredBccFetcher.fetch(container);
					if (filteredBcc != null) {
						filteredCount += tmpCount - filteredBcc.getBaseCall(variantBase);						
					} else {
						filteredCount += tmpCount;
					}
				}
			}

			// check if too much filteredCount
			if (filterByRatio.filter(count, filteredCount)) {
				return true;
			}
			
		}

		return false;
	}
	
	
	
}
