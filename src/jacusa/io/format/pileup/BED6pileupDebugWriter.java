package jacusa.io.format.pileup;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.format.BEDlikeResultWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasPileupCount;
import lib.data.has.filter.HasPileupFilterData;
import lib.data.result.Result;

public class BED6pileupDebugWriter<T extends AbstractData & HasPileupCount & HasPileupFilterData, R extends Result<T>> 
extends BEDlikeResultWriter<T, R> {

	public BED6pileupDebugWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	@Override
	protected String getStatistic(R result) {
		final ParallelData<T> parallelData = result.getParellelData();
		final int coverage = parallelData.getCombinedPooledData().getCoverage();
		return Integer.toString(coverage);
	}
	
	@Override
	protected void addHeaderConditionData(StringBuilder sb, int conditionIndex, int replicateIndex) {
		super.addHeaderConditionData(sb, conditionIndex, replicateIndex);

		for (final AbstractFilterFactory<?> filterFactory : getParameter().getFilterConfig().getFilterFactories()) {
			sb.append(SEP);
			sb.append(filterFactory.getC());
			sb.append(conditionIndex + 1);
			sb.append(replicateIndex + 1);
		}
	}
	
	@Override
	protected void addResultReplicateData(StringBuilder sb, T data) {
		super.addResultReplicateData(sb, data);
		for (final AbstractFilterFactory<T> filterFactory : getParameter().getFilterConfig().getFilterFactories()) {
			sb.append(SEP);
			filterFactory.addFilteredData(sb, data);
		}
	}
	
	@Override
	protected String getFieldName() {
		return "pileup";
	}
	
}