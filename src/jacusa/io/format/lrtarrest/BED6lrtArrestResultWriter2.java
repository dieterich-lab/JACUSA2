package jacusa.io.format.lrtarrest;

import java.util.Map;

import jacusa.io.format.BEDlikeResultWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.ResultWriterUtils;

public class BED6lrtArrestResultWriter2<T extends AbstractData & HasReferenceBase & HasBaseCallCount & HasLRTarrestCount, R extends Result<T> & hasStatistic> 
extends BEDlikeResultWriter<T, R> {
	
	// read start, trough, and end	
	private static final String INFO = "reads";
	private static final String REF2BC_INFO = "ref2bc";
	
	protected BED6lrtArrestResultWriter2(final String filename, final AbstractParameter<T, R> parameter) {
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
		addHeaderBases(sb, conditionIndex, replicateIndex);
		addHeaderReadInfo(sb, conditionIndex, replicateIndex);
	}

	protected void addHeaderBases(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		super.addHeaderBases(sb, conditionIndex, replicateIndex);
		sb.append(SEP);
		sb.append(REF2BC_INFO);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}
	
	@Override
	protected String getStatistic(final R result) {
		return Double.toString(result.getStatistic());
	}
	
	protected void addHeaderReadInfo(final StringBuilder sb, int conditionIndex, final int replicateIndex) {
		sb.append(SEP);
		sb.append(INFO);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}

	@Override
	protected void addResultReplicateData(final StringBuilder sb, final T data) {
		addResultBaseCallCount(sb, data);
		addResultReadInfoCount(sb, data);
	}

	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
		super.addResultBaseCallCount(sb, data);
		// output condition: Ax,Cx,Gx,Tx
		sb.append(SEP);

		final Map<Integer, BaseCallCount> ref2baseCallCount4arrest = data.getLRTarrestCount().getRefPos2bc4arrest();
		ResultWriterUtils.addResultRefPos2baseChange(sb, ref2baseCallCount4arrest);
	}

	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		sb.append(SEP);
		sb.append(data.getLRTarrestCount().getRTarrestCount().getReadArrest());
		sb.append(SEP2);
		sb.append(data.getLRTarrestCount().getRTarrestCount().getReadThrough());
	}

}