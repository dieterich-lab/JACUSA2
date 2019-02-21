package lib.stat.sample.provider.pileup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jacusa.JACUSA;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

public class InSilicoEstimationSamplePileupProvider 
extends AbstractEstimationSamplePileupProvider {
	
	public InSilicoEstimationSamplePileupProvider(
			final int maxIterations, final double estimatedError) {
		
		super(maxIterations, estimatedError);
	}

	@Override
	protected List<List<PileupCount>> process(final ParallelData parallelData) {
		final int ORIGINAL_CONDITION_INDEX = 0;
		final int INSILICO_CONDITION_INDEX = 1;
		
		final Base refBase = parallelData.getCombinedPooledData().getReferenceBase();
		final BaseCallCount bcc = JACUSA.BCC_FACTORY.create();
		bcc.merge(parallelData.getCombinedPooledData().getPileupCount().getBaseCallCount());
		final Set<Base> alleles = bcc.getAlleles();
		alleles.remove(refBase);

		// container for adjusted parallelPileup
		final List<PileupCount> pileupCounts = getPileupCounts(ORIGINAL_CONDITION_INDEX, parallelData);
		final List<List<PileupCount>> modifiedPileupCounts = new ArrayList<>(Arrays.asList(null, null));
		modifiedPileupCounts.set(ORIGINAL_CONDITION_INDEX, pileupCounts);
		modifiedPileupCounts.set(
				INSILICO_CONDITION_INDEX, 
				flat(
						pileupCounts,
						alleles, refBase) );
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
