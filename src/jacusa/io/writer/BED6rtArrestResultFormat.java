package jacusa.io.writer;


import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

public class BED6rtArrestResultFormat<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasRTarrestCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'D';

	protected BED6rtArrestResultFormat(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc, parameter);
	}

	public BED6rtArrestResultFormat(final AbstractParameter<T, R> parameter) {
		this(CHAR, "DEBUG", parameter);
	}

	@Override
	public ResultWriter<T, R> createWriter(final String filename) {
		return new BED6rtArrestResultWriter<T, R>(filename, getParameter());
	}

}