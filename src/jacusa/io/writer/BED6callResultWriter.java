package jacusa.io.writer;

import jacusa.cli.parameters.hasStatistic;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.Result;

public class BED6callResultWriter<T extends AbstractData & hasPileupCount, R extends Result<T> & hasStatistic> 
extends BEDlikeResultWriter<T, R> {

	public BED6callResultWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	@Override
	protected String getStatistic(R result) {
		return Double.toString(result.getStatistic());
	}
	
	@Override
	protected String getFieldName() {
		return "variant";
	}
	
}