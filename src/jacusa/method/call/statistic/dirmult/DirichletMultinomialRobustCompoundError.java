package jacusa.method.call.statistic.dirmult;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;

public class DirichletMultinomialRobustCompoundError<T extends BaseQualData>
extends DirichletMultinomialCompoundError<T> {

	public DirichletMultinomialRobustCompoundError(final CallParameters<T> parameters) {
		super(parameters);
	}

	@Override
	public String getName() {
		return "DirMult-RCE";
	}

	@Override
	public String getDescription() {
		return "Robust Compound Err.";  
	}

	@Override
	public double getStatistic(final ParallelPileupData<T> parallelData) {
		/* 
		 * check if any sample is homomorph and
		 * replace this sample with the other sample and make it homomorph 
		 */
		
		// determine the number of alleles per sample: 1, 2, and P 
		int a1 = parallelData.getPooledData(0).getBaseQualCount().getAlleles().length;
		int a2 = parallelData.getPooledData(1).getBaseQualCount().getAlleles().length;
		// all observed alleles
		int[] alleles = parallelData.getCombinedPooledData().getBaseQualCount().getAlleles();
		int aP = alleles.length;

		// get bases that are different between the samples
		int[] variantBaseIs = ParallelPileupData.getVariantBaseIndexs(parallelData);
		// if there are no variant bases than both samples are heteromorph; 
		// use existing parallelPileup to calculate test-statistic
		if (variantBaseIs.length == 0) {
			return super.getStatistic(parallelData);
		}

		// determine common base (shared by both conditions)
		int commonBaseIndex = -1;
		for (int baseIndex : alleles) {
			int count1 = parallelData.getPooledData(0).getBaseQualCount().getBaseCount(baseIndex);
			int count2 = parallelData.getPooledData(1).getBaseQualCount().getBaseCount(baseIndex);
			if (count1 > 0 && count2  > 0) {
				commonBaseIndex = baseIndex;
				break;
			}
		}

		T[][] data = parameters.getMethodFactory().createContainer(2);
		
		// container for adjusted parallelPileup
		ParallelPileupData<T> adjustedParallelPileup = null;
		// determine which condition has the variant base
		if (a1 > 1 && a2 == 1 && aP == 2) { // condition1
			
			System.arraycopy(data[0], 0, parallelData.getData(0), 0, parallelData.getData(0).length);
			System.arraycopy(data[1], 0, parallelData.getData(0), 0, parallelData.getData(0).length);

			adjustedParallelPileup = new ParallelPileupData<T>(
					parameters.getMethodFactory(),
					parallelData.getCoordinate(), data);
			// and replace pileups2 with pileups1 where the variant bases have been replaced with the common base
			T[] newConditionData = parameters.getMethodFactory().createReplicateData(adjustedParallelPileup.getData(0).length);
			adjustedParallelPileup.setData(0, ParallelPileupData.flat(adjustedParallelPileup.getData(0), newConditionData, variantBaseIs, commonBaseIndex));
		} else if (a2 > 1 && a1 == 1 && aP == 2) { // condition2
			
			System.arraycopy(data[0], 0, parallelData.getData(1), 0, parallelData.getData(1).length);
			System.arraycopy(data[1], 0, parallelData.getData(1), 0, parallelData.getData(1).length);
			
			adjustedParallelPileup = new ParallelPileupData<T>(
					parameters.getMethodFactory(),
					parallelData.getCoordinate(), data);
			T[] newConditionData = parameters.getMethodFactory().createReplicateData(adjustedParallelPileup.getData(1).length);
			adjustedParallelPileup.setData(1, ParallelPileupData.flat(adjustedParallelPileup.getData(1), newConditionData, variantBaseIs, commonBaseIndex));
		}
		// aP > 3, just use the existing parallelPileup to calculate the test-statistic
		if (adjustedParallelPileup == null) { 
			return super.getStatistic(parallelData);
		}
		
		return super.getStatistic(adjustedParallelPileup);
	}

}