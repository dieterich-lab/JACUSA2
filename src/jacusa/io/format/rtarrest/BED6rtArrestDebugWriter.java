package jacusa.io.format.rtarrest;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.result.Result;
import lib.data.result.hasStatistic;

public class BED6rtArrestDebugWriter<T extends AbstractData & HasReferenceBase & HasRTcount & HasArrestBaseCallCount & HasThroughBaseCallCount, R extends Result<T> & hasStatistic> 
extends BED6rtArrestResultWriter<T, R> {
	
	protected BED6rtArrestDebugWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	@Override
	protected void addHeaderConditionData(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		super.addHeaderConditionData(sb, conditionIndex, replicateIndex);
		addHeaderReadInfo(sb, conditionIndex, replicateIndex);
	}

	protected void addHeaderReadInfo(final StringBuilder sb, int conditionIndex, final int replicateIndex) {
		sb.append(FIELD_SEP);
		sb.append("readStart");
		sb.append(VALUE_SEP);
		sb.append("readInner");
		sb.append(VALUE_SEP);
		sb.append("readEnd");
	}

	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		sb.append(FIELD_SEP);
		sb.append(data.getRTarrestCount().getReadStart());
		sb.append(VALUE_SEP);
		sb.append(data.getRTarrestCount().getReadInternal());
		sb.append(VALUE_SEP);
		sb.append(data.getRTarrestCount().getReadEnd());
	}

}