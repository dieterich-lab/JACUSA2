package jacusa.io.format.pileup;

import jacusa.io.format.BEDlikeResultWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasPileupCount;
import lib.data.result.Result;

public class BED6pileupResultWriter<T extends AbstractData & HasPileupCount, R extends Result<T>> 
extends BEDlikeResultWriter<T, R> {

	public BED6pileupResultWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	@Override
	protected String getStatistic(R result) {
		final ParallelData<T> parallelData = result.getParellelData();
		final int coverage = parallelData.getCombinedPooledData().getCoverage();
		return Integer.toString(coverage);
	}
	
	@Override
	protected String getFieldName() {
		return "pileup";
	}
	
}