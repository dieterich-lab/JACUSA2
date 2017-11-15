package jacusa.io;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;

public class PileupWriter<T extends AbstractData, R extends Result<T>> 
extends AbstractResultFileWriter<T, R> {

	public PileupWriter(final String filename) {
		super(filename);
	}
	
	@Override
	public void writeHeader(final List<AbstractConditionParameter<T>> conditionParameter) {
		
	}
	
	@Override
	public void writeResult(R result) {
		
	}

}
