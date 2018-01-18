package jacusa.io.writer;

import jacusa.cli.parameters.hasStatistic;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;

public class BED6rtArrestResultWriter1<T extends AbstractData & hasReferenceBase & hasReadInfoCount, R extends Result<T> & hasStatistic> 
extends BEDlikeWriter<T, R> {
	
	// read start, trough, and end	
	private static final String RTinfo = "reads";

	protected BED6rtArrestResultWriter1(final String filename, final AbstractParameter<T, R> parameter) {
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
		addResultReadInfoCount(sb, data);
	}
	
	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		sb.append(SEP);
		sb.append(data.getReadInfoCount().getArrest());
		sb.append(SEP2);
		sb.append(data.getReadInfoCount().getThrough());
		
		if (getParameter().isDebug()) {
			sb.append(SEP);
			sb.append(data.getReadInfoCount().getStart());
			sb.append(SEP2);
			sb.append(data.getReadInfoCount().getInner());
			sb.append(SEP2);
			sb.append(data.getReadInfoCount().getEnd());
		}
	}

}