package lib.stat.estimation.provider.pileup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

public class InSilicoEstimationPileupProvider 
extends AbstractEstimationContainerProvider {
	
	public InSilicoEstimationPileupProvider(
			final boolean calcPValue, final int maxIterations, final double estimatedError) {
		
		super(calcPValue, maxIterations, estimatedError);
	}

	@Override
	protected List<List<PileupCount>> process(final ParallelData parallelData) {
		final Base refBase = parallelData.getCombPooledData().getAutoRefBase();
		final BaseCallCount bcc = BaseCallCount.create();
		bcc.merge(parallelData.getCombPooledData().getPileupCount().getBCC());
		final SortedSet<Base> alleles = new TreeSet<>(bcc.getAlleles());
		alleles.remove(refBase);

		// container for adjusted parallelPileup
		final List<PileupCount> pileupCounts = getPileupCounts(0, parallelData);
		final List<List<PileupCount>> modifiedPileupCounts = new ArrayList<>(Arrays.asList(null, null));
		modifiedPileupCounts.set(
				0, 
				flat(
						pileupCounts,
						alleles, refBase) );
		modifiedPileupCounts.set(1, pileupCounts);
		return modifiedPileupCounts;
	}
	
	private List<PileupCount> getPileupCounts(final int conditionIndex, final ParallelData parallelData) {
		final List<PileupCount> pileupCounts = 
				new ArrayList<>(parallelData.getReplicates().get(conditionIndex));
		for (final DataContainer container : parallelData.getData(conditionIndex)) {
			pileupCounts.add(container.getPileupCount());
		}
		return pileupCounts;
	}
	
}
