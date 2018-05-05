package jacusa.io.format.pileup;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFormat;

public class BED6pileupResultFormat<T extends AbstractData & HasPileupCount, R extends Result<T>> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'B';
	private AbstractParameter<T, R> parameter;
	
	public BED6pileupResultFormat(final AbstractParameter<T, R> parameter) {
		super(CHAR, "Default", parameter);
		
		this.parameter = parameter;
	}

	@Override
	public BED6pileupResultWriter<T, R> createWriter(final String filename) {
		return new BED6pileupResultWriter<T, R>(filename, parameter);
	}

}