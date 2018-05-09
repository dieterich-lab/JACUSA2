package jacusa.io.format.pileup;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.has.filter.HasBaseCallCountFilterData;
import lib.data.result.Result;
import lib.io.AbstractResultFormat;

public class BED6pileupDebugFormat<T extends AbstractData & HasPileupCount & HasBaseCallCountFilterData, R extends Result<T>> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'D';
	private AbstractParameter<T, R> parameter;
	
	public BED6pileupDebugFormat(final AbstractParameter<T, R> parameter) {
		super(CHAR, "Debug", parameter);
		this.parameter = parameter;
	}

	@Override
	public BED6pileupDebugWriter<T, R> createWriter(final String filename) {
		return new BED6pileupDebugWriter<T, R>(filename, parameter);
	}

}