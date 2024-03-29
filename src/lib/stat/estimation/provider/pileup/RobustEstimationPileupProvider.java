package lib.stat.estimation.provider.pileup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;

public class RobustEstimationPileupProvider 
extends AbstractEstimationContainerProvider {

	public RobustEstimationPileupProvider(
			final boolean calcPValue, final int maxIterations, final double estimatedError) {
		super(calcPValue, maxIterations, estimatedError);
	}

	@Override
	public List<List<PileupCount>> process(final ParallelData parallelData) {
		/* 
		 * check if any sample is homomorph and
		 * replace this sample with the other sample and make it homomorph 
		 */
		
		final PileupCount pileupCount1 = parallelData.getPooledData(0).getPileupCount();
		final PileupCount pileupCount2 = parallelData.getPooledData(1).getPileupCount();
		
		// determine the number of alleles per sample: 1, 2, and P 
		final int allelesSize1 = pileupCount1.getBCC().getAlleles().size();
		final int allelesSize2 = pileupCount2.getBCC().getAlleles().size();
		// all observed alleles
		final Set<Base> allelesPooled = new HashSet<>();
		allelesPooled.addAll(pileupCount1.getBCC().getAlleles());
		allelesPooled.addAll(pileupCount2.getBCC().getAlleles());
		final int allelesSizePooled = allelesPooled.size();

		// get bases that are different between the samples
		final List<BaseCallCount> originalBccs = new ArrayList<>(parallelData.getCombinedData().size());
		for (final DataContainer container : parallelData.getCombinedData()) {
			originalBccs.add(container.getPileupCount().getBCC());
		}
		final Set<Base> variantBases = 
				ParallelData.getVariantBases(
						allelesPooled,
						originalBccs);
		// if there are no variant bases than both samples are heteromorph; 
		// use existing parallelPileup to calculate test-statistic
		final List<List<PileupCount>> orginalPileupCounts = getPileupCounts(parallelData);
		if (variantBases.size() == 0) {
			return orginalPileupCounts;
		}
		
		int keepcondI 		= -1;
		int changecondI 	= -1;
		
		// determine which condition has the variant base
		if (allelesSize1 > 1 && allelesSize2 == 1 && allelesSizePooled == 2) { // condition1
			keepcondI 		= 0;
			changecondI 	= 1;
		} else if (allelesSize2 > 1 && allelesSize1 == 1 && allelesSizePooled == 2) { // condition2
			keepcondI 		= 1;
			changecondI 	= 0;
		}
		
		if (keepcondI >= 0 && 
				changecondI >= 0) {
			// determine common base (shared by both conditions)
			final Base commonBase = getCommonBase(allelesPooled, pileupCount1, pileupCount2);
									
			// container for adjusted parallelPileup
			final List<List<PileupCount>> modifiedPileupCounts = Arrays.asList(null, null);
			modifiedPileupCounts.set(keepcondI, orginalPileupCounts.get(keepcondI));
			modifiedPileupCounts.set(
					changecondI, 
					flat(
							orginalPileupCounts.get(keepcondI),
							variantBases, commonBase) );
			return modifiedPileupCounts;
		}
		
		// aP > 3, just use the existing parallelPileup to calculate the test-statistic		
		return orginalPileupCounts;
	}

	public List<PileupCount> flat(
			final List<PileupCount> pileupCounts, 
			final Set<Base> variantBases, final Base commonBase) {

		final List<PileupCount> flatPileupCounts = new ArrayList<>(pileupCounts.size());
		for (final PileupCount pileupCount : pileupCounts) {
			final PileupCount pileupCountCopy = pileupCount.copy();
			for (final Base variantBase : variantBases) {
				pileupCountCopy.add(commonBase, variantBase, pileupCount);
				pileupCountCopy.substract(variantBase, pileupCount);
				flatPileupCounts.add(pileupCountCopy);
			}
		}
		return flatPileupCounts;
	}
	
	private Base getCommonBase(final Set<Base> alleles, final PileupCount p1, PileupCount p2) {
		for (final Base base : alleles) {
			int count1 = p1.getBCC().getBaseCall(base);
			int count2 = p2.getBCC().getBaseCall(base);
			if (count1 > 0 && count2  > 0) {
				return base;
			}
		}
		
		return Base.N;
	}
	
}
