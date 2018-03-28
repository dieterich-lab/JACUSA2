package jacusa.io.writer;


import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.result.Result;
import lib.data.result.hasStatistic;

public class BED6callResultWriter<T extends AbstractData & HasPileupCount, R extends Result<T> & hasStatistic> 
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