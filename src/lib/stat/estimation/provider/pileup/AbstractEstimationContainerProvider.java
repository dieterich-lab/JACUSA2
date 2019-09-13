package lib.stat.estimation.provider.pileup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.phred2prob.Phred2Prob;
import lib.stat.estimation.DefaultEstimationContainer;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.stat.nominal.NominalData;
import lib.util.Base;

abstract class AbstractEstimationContainerProvider 
implements EstimationContainerProvider {

	private final boolean calcPValue; 
	private final int maxIters;
	private final double estError;
	
	AbstractEstimationContainerProvider(
			final boolean calcPValue,
			final int maxIters, 
			final double estError) {

		this.calcPValue		= calcPValue;
		this.maxIters 	= maxIters;
		this.estError	= estError;
	}

	protected abstract List<List<PileupCount>> process(ParallelData parallelData);
	
	private Base[] getBases(final ParallelData parallelData) {
		if (calcPValue) {
			return parallelData.getCombPooledData().getPileupCount().getBCC()
					.getAlleles().toArray(new Base[0]);
		}
		
		return Base.validValues();
	}
	
	@Override
	public EstimationContainer[] convert(final ParallelData parallelData) {
		final List<List<PileupCount>> pileupCounts = process(parallelData);

		final Base[] bases 		= getBases(parallelData);
		final int conditions 	= pileupCounts.size();
		final EstimationContainer[] estContainers = new EstimationContainer[conditions + 1];
		for (int condI = 0; condI < conditions; ++condI) {
			final NominalData nominalData 	= createData(bases, pileupCounts.get(condI)); 
			estContainers[condI] 	= createContainer(Integer.toString(condI + 1), nominalData, maxIters);
		}

		// conditions pooled
		final NominalData nominalData = createData(
				bases, 
				pileupCounts.stream()
					.flatMap(List::stream)
					.collect(Collectors.toList()) ); 
		estContainers[conditions] 		= new DefaultEstimationContainer("P", nominalData, maxIters);
		return estContainers;
	}

	private EstimationContainer createContainer(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultEstimationContainer(id, nominalData, maxIterations);
	}
	
	private NominalData createData(final Base[] bases, final List<PileupCount> pileupCounts) {
		final double[][] dataMatrix  = new double[pileupCounts.size()][bases.length];
		for (int replicateI = 0; replicateI < pileupCounts.size(); replicateI++) {
			populate(pileupCounts.get(replicateI), bases, dataMatrix[replicateI]);
		}
		return NominalData.build(bases.length, dataMatrix);
	}
	
	protected List<List<PileupCount>> getPileupCounts(final ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final List<List<PileupCount>> originalPileupCounts = new ArrayList<>(conditions);
		for (int condI = 0; condI < conditions; ++condI) {
			final List<PileupCount> tmpPileupCounts = new ArrayList<>(parallelData.getData(condI).size());
			for (final DataContainer container : parallelData.getData(condI)) {
				tmpPileupCounts.add(container.getPileupCount());
			}
			originalPileupCounts.add(tmpPileupCounts);
		}
		return originalPileupCounts;
	}
	
	
	
	protected List<PileupCount> flat(
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
	
	protected void populate(final PileupCount pileupCount, final Base[] bases, double[] pileupVector) {
		final Phred2Prob phred2Prob = Phred2Prob.getInstance(bases.length);
		final double[] colSumCount 	= phred2Prob.colSumCount(bases, pileupCount);
		final double[] colMeanError = phred2Prob.colMeanErrorProb(bases, pileupCount);

		for (final Base base : bases) {
			final int index = base.getIndex();
			if (colSumCount[index] > 0.0) {
				pileupVector[index] += colSumCount[index];
				for (final Base base2 : bases) {
					if (base != base2) {
						double combinedError = (colMeanError[base2.getIndex()] + estError) * colSumCount[index] / 
								(double)(bases.length - 1);
						pileupVector[base2.getIndex()] += combinedError;
					}
				}
			}
		}
	}

}

