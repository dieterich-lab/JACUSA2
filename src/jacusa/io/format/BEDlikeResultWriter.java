package jacusa.io.format;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.io.ResultWriterUtils;

/**
 * 
 * @author michael
 *
 * @param <T>
 * @param <R>
 */
public abstract class BEDlikeResultWriter<T extends AbstractData & HasBaseCallCount & HasReferenceBase, R extends Result<T>> 
extends BEDlikeWriter<T, R> {

	private static final String INFO = "bases";
	
	public BEDlikeResultWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	protected void addHeaderBases(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		sb.append(SEP);
		sb.append(INFO);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}

	protected abstract String getStatistic(final R result);

	@Override
	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
		ResultWriterUtils.addBaseCallCount(sb, data.getBaseCallCount());
	}
	
}
