package jacusa.io.writer;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;

/**
 * This class implements an extended BED6 format to represent variants identified by "call" method. 
 *
 * @param <T>
 * @param <R>
 */
public class BED6callResultFormat<T extends AbstractData & hasPileupCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	// unique char id for CLI 
	public static final char CHAR = 'B';
	
	public BED6callResultFormat(final AbstractParameter<T, R> parameter) {
		super(CHAR, "BED6-extended result format", parameter);
	}

	@Override
	public BED6callResultWriter<T, R> createWriter(final String filename) {
		return new BED6callResultWriter<T, R>(filename, getParameter());
	}

}