package jacusa.io.writer;

import java.util.Map;

import jacusa.cli.parameters.hasStatistic;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.has.hasReadInfoExtendedCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;

public class BED6lrtArrestResultWriter2<T extends AbstractData & hasReferenceBase & hasReadInfoExtendedCount, R extends Result<T> & hasStatistic> 
extends BEDlikeWriter<T, R> {
	
	public static final char SEP3 	= ':';
	public static final char SEP4 	= ';';
	public static final char SEP5 	= '=';
	
	// read start, trough, and end	
	private static final String RT_INFO = "reads";
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
		sb.append(RT_INFO);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}

	@Override
	protected void addResultReplicateData(final StringBuilder sb, final T data) {
		addResultBaseCallCount(sb, data);
		addResultReadInfoCount(sb, data);
	}

	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
		// output condition: Ax,Cx,Gx,Tx
		sb.append(SEP);

		final Map<Integer, BaseCallCount> ref2baseCallCount4arrest = data.getReadInfoExtendedCount().getRefPos2baseChange4arrest();
		addResultRefPos2baseChange(sb, ref2baseCallCount4arrest);
		/* FIXME what about through base changes
		sb.append(SEP5);
		final Map<Integer, BaseCallCount> ref2baseCallCount4through = data.getReadInfoExtendedCount().getRefPos2baseChange4through();
		addResultRefPos2baseChange(sb, ref2baseCallCount4through);
		*/
	}

	protected void addResultRefPos2baseChange(final StringBuilder sb, final Map<Integer, BaseCallCount> ref2baseCallCount) {
		final int n = ref2baseCallCount.size();
		if (n == 0) {
			sb.append(EMPTY);
			return;
		}
		int j = 0;
		for (final int refPos : ref2baseCallCount.keySet()) {
			final BaseCallCount baseCallCount = ref2baseCallCount.get(refPos);

			sb.append(refPos);
			sb.append(SEP3);

			int baseIndex = 0;
			int count = 0;
			if (baseIndex >= 0) {
				count = baseCallCount.getBaseCallCount(baseIndex);
			}
			sb.append(count);
			++baseIndex;
			for (; baseIndex < BaseCallConfig.BASES.length; ++baseIndex) {
				count = 0;
				if (baseIndex >= 0) {
					count = baseCallCount.getBaseCallCount(baseIndex);
				}
				sb.append(SEP2);
				sb.append(count);
			}

			++j;
			if (j < n) {
				sb.append(SEP4);
			}
		}		
	}

	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		sb.append(SEP);
		sb.append(data.getReadInfoExtendedCount().getArrest());
		sb.append(SEP2);
		sb.append(data.getReadInfoExtendedCount().getThrough());
	}

}