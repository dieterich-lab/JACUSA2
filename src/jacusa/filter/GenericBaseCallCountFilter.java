package jacusa.filter;

import java.util.List;
import java.util.Set;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

/**
 * Class that enables filtering based on base call count data and some other
 * filter cached data.
 */
public class GenericBaseCallCountFilter extends AbstractFilter {

	private final DataType<BaseCallCount> observedDataType;
	private final DataType<BaseCallCount> filteredDataType;

	private final FilterByRatio filterByRatio;

	public GenericBaseCallCountFilter(final char id, final DataType<BaseCallCount> observedDataType,
			final DataType<BaseCallCount> filteredDataType, final int overhang,
			final FilterByRatio filterByRatio) {

		super(id, overhang);
		this.observedDataType = observedDataType;
		this.filteredDataType = filteredDataType;

		this.filterByRatio = filterByRatio;
	}

	/**
	 * Tested in @see test.jacusa.filtering.GenericBaseCallCountFilterTest
	 */
	@Override
	public boolean filter(final ParallelData parallelData) {
		final BaseCallCount bcc = parallelData.getCombPooledData().get(observedDataType);
		final Set<Base> alleles = bcc.getAlleles();

		Set<Base> variantBases = null;
		// depending on the number of conditions define the set of variants to be tested
		// for
		// false positive variants
		if (parallelData.getConditions() == 1) {
			variantBases = ParallelData.getNonReferenceBases(parallelData.getCombPooledData().getAutoRefBase());
		} else if (parallelData.getConditions() == 2) {
			final BaseCallCount pooledBcc1 = parallelData.getPooledData(0).get(observedDataType);
			final BaseCallCount pooledBcc2 = parallelData.getPooledData(1).get(observedDataType);
			variantBases = ParallelData.getVariantBases(pooledBcc1, pooledBcc2);
		} else { // for future version that support > 2 conditions
			final List<BaseCallCount> bccs = observedDataType.extract(parallelData.getCombinedData());
			variantBases = ParallelData.getVariantBases(alleles, bccs);
		}

		return filter(variantBases, parallelData);
	}

	// For each variant base check if filteredCount / observed count >= minRatio,
	// otherwise filter
	private boolean filter(final Set<Base> variantBases, final ParallelData parallelData) {
		for (final Base variantBase : variantBases) {
			int count = 0;
			int filteredCount = 0;

			for (int condI = 0; condI < parallelData.getConditions(); ++condI) {
				final int replicates = parallelData.getReplicates(condI);
				for (int replicateI = 0; replicateI < replicates; replicateI++) {
					final DataContainer container = parallelData.getDataContainer(condI, replicateI);
					// observed count
					final BaseCallCount observedbcc = container.get(observedDataType);
					final int observed = observedbcc.getBaseCall(variantBase);
					count += observed;
					// artefacts
					final BaseCallCount filteredBcc = container.get(filteredDataType);
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
