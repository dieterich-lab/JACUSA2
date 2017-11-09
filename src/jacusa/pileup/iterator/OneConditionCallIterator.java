package jacusa.pileup.iterator;

import lib.data.basecall.PileupData;

public class OneConditionCallIterator<T extends PileupData> { 

	// FIXME
	/*
	public ParallelData<T> next() {
		ParallelData<T> parallelData = super.next();
		
		T data = parallelData.getCombinedPooledData();
		int[] allelesIndexs = data.getBaseQualCount().getAlleles();

		// pick reference base by MD or by majority.
		// all other bases will be converted in pileup2 to refBaseI
		int refBaseIndex = -1;
		if (data.getReferenceBase() != 'N') {
			char refBase = data.getReferenceBase();
			refBaseIndex = BaseCallConfig.BASES[(byte)refBase];
		} else {
			int maxBaseCount = 0;

			for (int baseIndex : allelesIndexs) {
				int count = data.getBaseQualCount().getBaseCount(baseIndex);
				if (count > maxBaseCount) {
					maxBaseCount = count;
					refBaseIndex = baseIndex;
				}
			}
		}

		// store non-reference base calls in variantBasesIndexs 
		int [] tmpVariantBasesIndexs = new int[allelesIndexs.length];
		int i = 0;
		for (int j = 0; j < allelesIndexs.length; ++j) {
			if (allelesIndexs[j] != refBaseIndex) {
				tmpVariantBasesIndexs[i] = allelesIndexs[j];
				++i;
			}
		}
		int[] variantBasesIndexs = Arrays.copyOf(tmpVariantBasesIndexs, i);
		
		// create fake condition by replacing non-reference base calls with reference BCs 
		T[] fakeCondition = getGeneralParameter().getMethodFactory().createReplicateData(parallelData.getReplicates(0));
		for (int replicateIndex = 0; replicateIndex < fakeCondition.length; ++replicateIndex) {
			fakeCondition[replicateIndex] = getGeneralParameter().getMethodFactory().createData();
			fakeCondition[replicateIndex].setCoordinate(new Coordinate(data.getCoordinate()));
			fakeCondition[replicateIndex].setReferenceBase(data.getReferenceBase());

			for (int variantBaseIndex : variantBasesIndexs) {
				fakeCondition[replicateIndex].getBaseQualCount()
					.add(refBaseIndex, variantBaseIndex, parallelData.getData(0, replicateIndex).getBaseQualCount());
				fakeCondition[replicateIndex].getBaseQualCount()
					.substract(variantBaseIndex, variantBaseIndex, parallelData.getData(0, replicateIndex).getBaseQualCount());
			}
		}
		
		ParallelData<T> newParallelPileupData = new ParallelData<T>(parallelData);
		newParallelPileupData.setData(1, fakeCondition);

		return newParallelPileupData;
	}
	*/

}
