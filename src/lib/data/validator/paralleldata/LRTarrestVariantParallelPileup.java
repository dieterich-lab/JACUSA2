package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;

public class LRTarrestVariantParallelPileup implements ParallelDataValidator {

	private final DataType<ArrestPosition2BaseCallCount> dataType;

	public LRTarrestVariantParallelPileup(final DataType<ArrestPosition2BaseCallCount> dataType) {
		this.dataType = dataType;
	}

	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer combinedPooledContainer = parallelData.getCombPooledData();

		final ArrestPosition2BaseCallCount ap2bcc = combinedPooledContainer.get(dataType);

		final int onePosition = parallelData.getCoordinate().get1Position();
		final BaseCallCount arrestBcc = ap2bcc.getArrestBCC(onePosition);
		final BaseCallCount throughBcc = ap2bcc.getThroughBCC(onePosition);

		return arrestBcc.getCoverage() > 0 && throughBcc.getCoverage() > 0;
	}

}