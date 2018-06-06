package jacusa.io.format;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.io.ResultWriterUtils;
import lib.util.Util;

/**
 * 
 * @author michael
 *
 * @param <T>
 * @param <R>
 */
public abstract class BEDlikeResultWriter<T extends AbstractData & HasBaseCallCount & HasReferenceBase, R extends Result<T>> 
extends BEDlikeWriter<T, R> {

	private static final String BASE_FIELD = "bases";
	
	public BEDlikeResultWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}
	
	@Override
	protected void addHeaderBases(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		sb.append(Util.FIELD_SEP);
		sb.append(BASE_FIELD);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}

	@Override
	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
		sb.append(Util.FIELD_SEP);
		ResultWriterUtils.addBaseCallCount(sb, data.getBaseCallCount());
	}
	
}
