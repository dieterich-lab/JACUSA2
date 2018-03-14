package jacusa.io.writer;


import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;
import lib.data.result.hasStatistic;

public class BED6rtArrestResultWriter<T extends AbstractData & hasBaseCallCount & hasReferenceBase & hasRTarrestCount, R extends Result<T> & hasStatistic> 
extends BEDlikeResultWriter<T, R> {
	
	// read start, trough, and end	
	private static final String RTinfo = "reads";

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
		sb.append(RTinfo);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
		
		if (getParameter().isDebug()) {
			sb.append(SEP);
			sb.append("readStart");
			sb.append(SEP2);
			sb.append("readInner");
			sb.append(SEP2);
			sb.append("readEnd");
		}
	}

	@Override
	protected void addResultReplicateData(final StringBuilder sb, final T data) {
		super.addResultReplicateData(sb, data);
		addResultReadInfoCount(sb, data);
	}
	
	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		sb.append(SEP);
		sb.append(data.getRTarrestCount().getReadArrest());
		sb.append(SEP2);
		sb.append(data.getRTarrestCount().getReadThrough());
		
		if (getParameter().isDebug()) {
			sb.append(SEP);
			sb.append(data.getRTarrestCount().getReadStart());
			sb.append(SEP2);
			sb.append(data.getRTarrestCount().getReadInternal());
			sb.append(SEP2);
			sb.append(data.getRTarrestCount().getReadEnd());
		}
	}

}