package jacusa.io.format.lrtarrest;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;

public class BED6lrtArrestDebugFormat<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasRefPos2BaseCallCountFilterData, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'D';

	protected BED6lrtArrestDebugFormat(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc, parameter);
	}

	public BED6lrtArrestDebugFormat(final AbstractParameter<T, R> parameter) {
		this(CHAR, "Debug - linkage arrest to base substitution and filter", parameter);
	}

	@Override
	public BED6lrtArrestDebugWriter<T, R> createWriter(final String filename) {
		return new BED6lrtArrestDebugWriter<T, R>(filename, getParameter());
	}

}