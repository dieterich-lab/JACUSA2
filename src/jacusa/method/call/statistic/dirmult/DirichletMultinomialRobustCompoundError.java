package jacusa.method.call.statistic.dirmult;

import java.util.Set;

import jacusa.cli.parameters.CallParameter;
import jacusa.method.call.statistic.AbstractDirichletStatistic;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasPileupCount;

public class DirichletMultinomialRobustCompoundError<T extends AbstractData & HasBaseCallCount & HasPileupCount>
extends DirichletMultinomialCompoundError<T> {

	public DirichletMultinomialRobustCompoundError(final CallParameter parameters) {
		super("DirMult-RCE", "Robust Compound Err.", parameters);
	}

	@Override
	public double getStatistic(final ParallelData<T> parallelData) {
		/* 
		 * check if any sample is homomorph and
		 * replace this sample with the other sample and make it homomorph 
		 */
		
		// determine the number of alleles per sample: 1, 2, and P 
		final int a1 = parallelData.getPooledData(0).getPileupCount().getBaseCallCount().getAlleles().size();
		final int a2 = parallelData.getPooledData(1).getPileupCount().getBaseCallCount().getAlleles().size();
		// all observed alleles
		final Set<Base> alleles = parallelData.getCombinedPooledData().getPileupCount().getBaseCallCount().getAlleles();
		final int aP = alleles.size();

		// get bases that are different between the samples
		final Set<Base> variantBases = ParallelData.getVariantBases(parallelData);
		// if there are no variant bases than both samples are heteromorph; 
		// use existing parallelPileup to calculate test-statistic
		if (variantBases.size() == 0) {
			return super.getStatistic(parallelData);
		}

		// determine common base (shared by both conditions)
		Base commonBase = Base.N;
		for (final Base base : alleles) {
			int count1 = parallelData.getPooledData(0).getBaseCallCount().getBaseCall(base);
			int count2 = parallelData.getPooledData(1).getBaseCallCount().getBaseCall(base);
			if (count1 > 0 && count2  > 0) {
				commonBase = base;
				break;
			}
		}

		final T[][] data = parallelData.getDataGenerator().createContainerData(2);

		// container for adjusted parallelPileup
		ParallelData<T> adjustedParallelPileup = null;
		// determine which condition has the variant base
		if (a1 > 1 && a2 == 1 && aP == 2) { // condition1
			
			data[0] = parallelData.getDataGenerator().copyReplicateData(parallelData.getData(0));
			data[1] = parallelData.getDataGenerator().copyReplicateData(parallelData.getData(0));

			adjustedParallelPileup = new ParallelData<T>(
					parallelData.getDataGenerator(), data);
			// and replace pileups2 with pileups1 where the variant bases have been replaced with the common base
			T[] newConditionData = parallelData.getDataGenerator().createReplicateData(adjustedParallelPileup.getData(0).length);
			adjustedParallelPileup.setData(0, ParallelData.flat(adjustedParallelPileup.getData(0), newConditionData, variantBases, commonBase));
		} else if (a2 > 1 && a1 == 1 && aP == 2) { // condition2
			
			data[0] = parallelData.getDataGenerator().copyReplicateData(parallelData.getData(1));
			data[1] = parallelData.getDataGenerator().copyReplicateData(parallelData.getData(1));
			
			adjustedParallelPileup = new ParallelData<T>(
					parallelData.getDataGenerator(), data);
			T[] newConditionData = parallelData.getDataGenerator().createReplicateData(adjustedParallelPileup.getData(1).length);
			adjustedParallelPileup.setData(1, ParallelData.flat(adjustedParallelPileup.getData(1), newConditionData, variantBases, commonBase));
		}
		// aP > 3, just use the existing parallelPileup to calculate the test-statistic
		if (adjustedParallelPileup == null) { 
			return super.getStatistic(parallelData);
		}
		
		return super.getStatistic(adjustedParallelPileup);
	}

	@Override
	public AbstractDirichletStatistic<T> newInstance() {
		return new DirichletMultinomialRobustCompoundError<T>(parameter);
	}
	
}