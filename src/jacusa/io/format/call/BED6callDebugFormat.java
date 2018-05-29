package jacusa.io.format.call;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.has.filter.HasBaseCallCountFilterData;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.AbstractResultFormat;

public class BED6callDebugFormat<T extends AbstractData & HasPileupCount & HasBaseCallCountFilterData, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'D';
	private AbstractParameter<T, R> parameter;
	
	public BED6callDebugFormat(final AbstractParameter<T, R> parameter) {
		super(CHAR, "Debug", parameter);
		this.parameter = parameter;
	}

	@Override
	public BED6callDebugWriter<T, R> createWriter(final String filename) {
		return new BED6callDebugWriter<T, R>(filename, parameter);
	}

}