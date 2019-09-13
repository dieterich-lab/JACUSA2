package jacusa.filter;

import java.util.List;
import java.util.Set;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.util.Base;

/**
 * Class that enables filtering based on base call count data and some other 
 * filter cached data.
 */
public class GenericBaseCallCountFilter extends AbstractFilter {

	private final Fetcher<BaseCallCount> observedBccFetcher;
	private final Fetcher<BaseCallCount> filteredBccFetcher;
	
	private final FilterByRatio filterByRatio;
	
	public GenericBaseCallCountFilter(
			final char id, 
			final Fetcher<BaseCallCount> observedBccFetcher,
			final Fetcher<BaseCallCount> filteredBccFetcher,
			final int overhang, 
			final FilterByRatio filterByRatio) {
		
		super(id, overhang);
		this.observedBccFetcher 	= observedBccFetcher;
		this.filteredBccFetcher 	= filteredBccFetcher;
		
		this.filterByRatio 			= filterByRatio;
	}

	/**
	 * Tested in @see test.jacusa.filtering.GenericBaseCallCountFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		final BaseCallCount bcc = observedBccFetcher.fetch(parallelData.getCombPooledData());
		final Set<Base> alleles = bcc.getAlleles();
		
		Set<Base> variantBases = null;
		// depending on the number of conditions define the set of variants to be tested for 
		// false positive variants
		if (parallelData.getConditions() == 1) {
			variantBases = ParallelData.getNonReferenceBases(
					parallelData.getCombPooledData().getAutoRefBase());
		} else if (parallelData.getConditions() == 2){
			final BaseCallCount pooledBcc1 = observedBccFetcher.fetch(parallelData.getPooledData(0));
			final BaseCallCount pooledBcc2 = observedBccFetcher.fetch(parallelData.getPooledData(1));
			variantBases = ParallelData.getVariantBases(pooledBcc1, pooledBcc2);
		} else { // for future version that support > 2 conditions
			final List<BaseCallCount> bccs = Fetcher.apply(observedBccFetcher, parallelData.getCombinedData());
			variantBases = ParallelData.getVariantBases(alleles, bccs);
		}

		return filter(variantBases, parallelData);
	}

	// For each variant base check if filteredCount / observed count >= minRatio, otherwise filter 
	private boolean filter(final Set<Base> variantBases, final ParallelData parallelData) {
		for (final Base variantBase : variantBases) {
			int count 			= 0;
			int filteredCount 	= 0;

			for (int condI = 0; condI < parallelData.getConditions(); ++condI) {
				final int replicates = parallelData.getReplicates(condI);
				for (int replicateI = 0; replicateI < replicates; replicateI++) {
					final DataContainer container = parallelData.getDataContainer(condI, replicateI);
					// observed count
					final BaseCallCount observedbcc = observedBccFetcher.fetch(container);
					final int observed 				= observedbcc.getBaseCall(variantBase);
					count += observed;
					// artefacts
					final BaseCallCount filteredBcc = filteredBccFetcher.fetch(container);
					if (filteredBcc.getCoverage() > 0) {
						// remaining = observed - artefacts
						filteredCount += observed - filteredBcc.getBaseCall(variantBase);
					} else {
						// remaining = observed
						filteredCount += observed;
					}
				}
			}

			if (filterByRatio.filter(count, filteredCount)) {
				return true;
			}
			
		}

		return false;
	}
	
}
