package lib.stat.sample.provider.pileup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.phred2prob.Phred2Prob;
import lib.stat.nominal.NominalData;
import lib.stat.sample.DefaultEstimationSample;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.util.Base;

abstract class AbstractEstimationSamplePileupProvider 
implements EstimationSampleProvider {

	protected static final int[][][] VALID;
	static {
		VALID 			= new int[5][][];
		VALID[0] 		= null; // not possible
		VALID[1] 		= null; // not possible

		VALID[2]		= new int[2][1];
		VALID[2][0][0] 	= 1;
		VALID[2][1][0] 	= 0;

		VALID[3]		= new int[3][2];
		VALID[3][0]		= new int[] {1, 2};
		VALID[3][1]		= new int[] {0, 2};
		VALID[3][2]		= new int[] {0, 1};

		VALID[4]		= new int[4][3];
		VALID[4][0]		= new int[] {1, 2, 3};
		VALID[4][1]		= new int[] {0, 2, 3};
		VALID[4][2]		= new int[] {0, 1, 3};
		VALID[4][3]		= new int[] {0, 1, 2};
	}
	
	private final int maxIterations;
	private final double estimatedError;
	
	public AbstractEstimationSamplePileupProvider(
			final int maxIterations, 
			final double estimatedError) {

		this.maxIterations 	= maxIterations;
		this.estimatedError	= estimatedError;
	}

	protected abstract List<List<PileupCount>> process(ParallelData parallelData);
	
	@Override
	public EstimationSample[] convert(final ParallelData parallelData) {
		final List<List<PileupCount>> pileupCounts = process(parallelData);

		final Base[] bases 		= Base.validValues();
		final int conditions 	= pileupCounts.size();
		final EstimationSample[] estimationSamples = new EstimationSample[conditions + 1];
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final NominalData nominalData 		= createData(bases, pileupCounts.get(conditionIndex)); 
			estimationSamples[conditionIndex] 	= createSample(Integer.toString(conditionIndex + 1), nominalData, maxIterations);
		}

		// conditions pooled
		final NominalData nominalData = createData(
				bases, 
				pileupCounts.stream()
					.flatMap(List::stream)
					.collect(Collectors.toList()) ); 
		estimationSamples[conditions] 		= new DefaultEstimationSample("P", nominalData, maxIterations);
		return estimationSamples;
	}

	private EstimationSample createSample(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultEstimationSample(id, nominalData, maxIterations);
	}
	
	private NominalData createData(final Base[] bases, final List<PileupCount> pileupCounts) {
		final double[][] dataMatrix  = new double[pileupCounts.size()][bases.length];
		for (int replicateIndex = 0; replicateIndex < pileupCounts.size(); replicateIndex++) {
			populate(pileupCounts.get(replicateIndex), bases, dataMatrix[replicateIndex]);
		}
		return NominalData.build(bases.length, dataMatrix);
	}
	
	protected List<List<PileupCount>> getPileupCounts(final ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final List<List<PileupCount>> originalPileupCounts = new ArrayList<>(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final List<PileupCount> tmpPileupCounts = new ArrayList<>(parallelData.getData(conditionIndex).size());
			for (final DataContainer container : parallelData.getData(conditionIndex)) {
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
				for (final int index2 : VALID[bases.length][index]) {
					double combinedError = (colMeanError[index2] + estimatedError) * (double)colSumCount[index] / 
							(double)(VALID[bases.length][index].length);
					pileupVector[index2] += combinedError;
				}
			}
		}
	}

}

