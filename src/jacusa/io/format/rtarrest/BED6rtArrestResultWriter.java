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
	
	// read start, trough, and end	
	private static final String RTinfo = "reads";
	
	private static final String ARREST_BASES = "arrest_bases";
	private static final String THROUGH_BASES = "through_bases";

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
	protected void addHeaderConditionData(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		super.addHeaderConditionData(sb, conditionIndex, replicateIndex);
		addHeaderReadInfo(sb, conditionIndex, replicateIndex);
	}

	@Override
	protected String getStatistic(final R result) {
		return Double.toString(result.getStatistic());
	}

	protected void addHeaderReadInfo(final StringBuilder sb, int conditionIndex, final int replicateIndex) {
		sb.append(SEP);
		
		if (getParameter().isDebug()) {
			sb.append(SEP);
			sb.append("readStart");
			sb.append(SEP2);
			sb.append("readInner");
			sb.append(SEP2);
			sb.append("readEnd");
		} else {
			sb.append(RTinfo);
			sb.append(conditionIndex + 1);
			sb.append(replicateIndex + 1);			
		}
	}

	@Override
	protected void addResultReplicateData(final StringBuilder sb, final T data) {
		super.addResultReplicateData(sb, data);
		addResultReadInfoCount(sb, data);
	}
	
	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
		ResultWriterUtils.addBaseCallCount(sb, data.getArrestBaseCallCount());
		sb.append(SEP);
		ResultWriterUtils.addBaseCallCount(sb, data.getThroughBaseCallCount());
	}

	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		if (getParameter().isDebug()) {
			sb.append(SEP);
			sb.append(data.getRTarrestCount().getReadStart());
			sb.append(SEP2);
			sb.append(data.getRTarrestCount().getReadInternal());
			sb.append(SEP2);
			sb.append(data.getRTarrestCount().getReadEnd());
		} else {
			sb.append(SEP);
			sb.append(data.getRTarrestCount().getReadArrest());
			sb.append(SEP2);
			sb.append(data.getRTarrestCount().getReadThrough());			
		}
	}

}