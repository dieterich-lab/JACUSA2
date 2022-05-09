package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;

public class RTarrestParallelPileup
implements ParallelDataValidator {
	
	private final ExtendedVariantSiteValidator variantSite;
	private final DataType<BaseCallCount> arrestDataType;
	private final DataType<BaseCallCount> throughDataType;

	public RTarrestParallelPileup(
			final DataType<BaseCallCount> totalBccFetcher, // FIXME
			final DataType<BaseCallCount> arrestDataType,
			final DataType<BaseCallCount> throughDataType) {

		// TODO add new derived datatype sum of arrestDataType, throughDataType
		this.variantSite = new ExtendedVariantSiteValidator(totalBccFetcher);
		this.arrestDataType 	= arrestDataType;
		this.throughDataType 	= throughDataType;
		
	}

	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer combinedPooledContainer = parallelData.getCombPooledData();
		return variantSite.isValid(parallelData) || 
				combinedPooledContainer.get(arrestDataType).getCoverage() > 0 &&
				combinedPooledContainer.get(throughDataType).getCoverage() > 0;
	}

}