package lib.io;


import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;

public interface ResultWriter<T extends AbstractData, R extends Result<T>> 
extends Closeable {

	void writeLine(String line);
	void writeHeader(List<AbstractConditionParameter<T>> conditionParameter);
	void writeResult(R result);
	void close() throws IOException;
	
	String getInfo();

}
