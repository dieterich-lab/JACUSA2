package jacusa.method.call.statistic.dirmult;

import jacusa.cli.parameters.CallParameter;
import jacusa.method.call.statistic.AbstractDirichletStatistic;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasPileupCount;

public class DirichletMultinomialRobustCompoundError<T extends AbstractData & HasBaseCallCount & HasPileupCount>
extends AbstractDirichletStatistic<T> {

	private DirichletMultinomialCompoundError<T> o;
	
	public DirichletMultinomialRobustCompoundError(final CallParameter parameters) {
		super("DirMult-RCE", "Robust Compound Err.", parameters);
		o = new DirichletMultinomialCompoundError<T>(parameters);
	}

	@Override
	public double getStatistic(final ParallelData<T> parallelData) {
		/* 
		 * check if any sample is homomorph and
		 * replace this sample with the other sample and make it homomorph 
		 */
		
		// determine the number of alleles per sample: 1, 2, and P 
		int a1 = parallelData.getPooledData(0).getPileupCount().getBaseCallCount().getAlleles().length;
		int a2 = parallelData.getPooledData(1).getPileupCount().getBaseCallCount().getAlleles().length;
		// all observed alleles
		int[] alleles = parallelData.getCombinedPooledData().getPileupCount().getBaseCallCount().getAlleles();
		int aP = alleles.length;

		// get bases that are different between the samples
		int[] variantBaseIs = ParallelData.getVariantBaseIndexs(parallelData);
		// if there are no variant bases than both samples are heteromorph; 
		// use existing parallelPileup to calculate test-statistic
		if (variantBaseIs.length == 0) {
			return super.getStatistic(parallelData);
		}

		// determine common base (shared by both conditions)
		int commonBaseIndex = -1;
		for (int baseIndex : alleles) {
			int count1 = parallelData.getPooledData(0).getBaseCallCount().getBaseCallCount(baseIndex);
			int count2 = parallelData.getPooledData(1).getBaseCallCount().getBaseCallCount(baseIndex);
			if (count1 > 0 && count2  > 0) {
				commonBaseIndex = baseIndex;
				break;
			}
		}

		T[][] data = parallelData.getDataGenerator().createContainerData(2);
		
		// container for adjusted parallelPileup
		ParallelData<T> adjustedParallelPileup = null;
		// determine which condition has the variant base
		if (a1 > 1 && a2 == 1 && aP == 2) { // condition1
			
			System.arraycopy(data[0], 0, parallelData.getData(0), 0, parallelData.getData(0).length);
			System.arraycopy(data[1], 0, parallelData.getData(0), 0, parallelData.getData(0).length);

			adjustedParallelPileup = new ParallelData<T>(
					parallelData.getDataGenerator(), data);
			// and replace pileups2 with pileups1 where the variant bases have been replaced with the common base
			T[] newConditionData = parallelData.getDataGenerator().createReplicateData(adjustedParallelPileup.getData(0).length);
			adjustedParallelPileup.setData(0, ParallelData.flat(adjustedParallelPileup.getData(0), newConditionData, variantBaseIs, commonBaseIndex));
		} else if (a2 > 1 && a1 == 1 && aP == 2) { // condition2
			
			System.arraycopy(data[0], 0, parallelData.getData(1), 0, parallelData.getData(1).length);
			System.arraycopy(data[1], 0, parallelData.getData(1), 0, parallelData.getData(1).length);
			
			adjustedParallelPileup = new ParallelData<T>(
					parallelData.getDataGenerator(), data);
			T[] newConditionData = parallelData.getDataGenerator().createReplicateData(adjustedParallelPileup.getData(1).length);
			adjustedParallelPileup.setData(1, ParallelData.flat(adjustedParallelPileup.getData(1), newConditionData, variantBaseIs, commonBaseIndex));
		}
		// aP > 3, just use the existing parallelPileup to calculate the test-statistic
		if (adjustedParallelPileup == null) { 
			return super.getStatistic(parallelData);
		}
		
		return super.getStatistic(adjustedParallelPileup);
	}

	@Override
	protected void populate(T data, int[] baseIndexs, double[] dataVector) {
		o.populate(data, baseIndexs, dataVector);
	}	
	
	@Override
	public AbstractStatisticCalculator<T> newInstance() {
		return new DirichletMultinomialRobustCompoundError<T>(parameter);
	}
	
}