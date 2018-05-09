package jacusa.io.format.rtarrest;


import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

public class BED6rtArrestResultFormat1<T extends AbstractData & HasReferenceBase & HasRTcount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'A';

	protected BED6rtArrestResultFormat1(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc, parameter);
	}

	public BED6rtArrestResultFormat1(final AbstractParameter<T, R> parameter) {
		this(CHAR, "Format 1 - arrest only", parameter);
	}

	@Override
	public ResultWriter<T, R> createWriter(final String filename) {
		return new BED6rtArrestResultWriter1<T, R>(filename, getParameter());
	}

}