package jacusa.io.format.rtarrest;

import jacusa.io.format.BEDlikeWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.ResultWriterUtils;

public class BED6rtArrestResultWriter<T extends AbstractData & HasReferenceBase & HasRTcount & HasArrestBaseCallCount & HasThroughBaseCallCount, R extends Result<T> & hasStatistic> 
extends BEDlikeWriter<T, R> {
	
	private static final String ARREST_BASES 	= "arrest_bases";
	private static final String THROUGH_BASES 	= "through_bases";

	protected BED6rtArrestResultWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	@Override
	protected String getHeaderStat() {
		return "pvalue";
	}
	
	@Override
	protected String getFieldName() {
		return "arrest";
	}
	
	protected void addHeaderBases(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		sb.append(SEP);
		sb.append(ARREST_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);

		sb.append(SEP);
		sb.append(THROUGH_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}

	@Override
	protected String getStatistic(final R result) {
		return Double.toString(result.getStatistic());
	}

	@Override
	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
		sb.append(SEP);
		ResultWriterUtils.addBaseCallCount(sb, data.getArrestBaseCallCount());
		sb.append(SEP);
		ResultWriterUtils.addBaseCallCount(sb, data.getThroughBaseCallCount());
	}
}