package jacusa.io.format.rtarrest;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

public class BED6rtArrestDebugFormat<T extends AbstractData & HasReferenceBase & HasRTcount & HasArrestBaseCallCount & HasThroughBaseCallCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'D';

	protected BED6rtArrestDebugFormat(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc, parameter);
	}

	public BED6rtArrestDebugFormat(final AbstractParameter<T, R> parameter) {
		this(CHAR, "DEBUG", parameter);
	}

	@Override
	public ResultWriter<T, R> createWriter(final String filename) {
		return new BED6rtArrestDebugWriter<T, R>(filename, getParameter());
	}

}