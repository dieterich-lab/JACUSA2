package jacusa.io.writer;


import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

public class BED6lrtArrestResultFormat2<T extends AbstractData & hasReferenceBase & hasLRTarrestCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'L';

	protected BED6lrtArrestResultFormat2(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc, parameter);
	}

	public BED6lrtArrestResultFormat2(final AbstractParameter<T, R> parameter) {
		this(CHAR, "Format 2 - linkage arrest to base substitution", parameter);
	}

	@Override
	public ResultWriter<T, R> createWriter(final String filename) {
		return new BED6lrtArrestResultWriter2<T, R>(filename, getParameter());
	}

}