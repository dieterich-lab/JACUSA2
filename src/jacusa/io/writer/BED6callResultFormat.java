package jacusa.io.writer;

import jacusa.cli.parameters.hasStatistic;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFormat;

public class BED6callResultFormat<T extends AbstractData & hasPileupCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'B';
	
	public BED6callResultFormat(final AbstractParameter<T, R> parameter) {
		super(CHAR, "Default", parameter);
	}

	@Override
	public BED6callResultWriter<T, R> createWriter(final String filename) {
		return new BED6callResultWriter<T, R>(filename, getParameter());
	}

}