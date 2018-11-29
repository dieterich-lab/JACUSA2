package lib.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.result.Result;

public interface ResultWriter 
extends Closeable {

	String getInfo();

	void writeHeader(List<AbstractConditionParameter> conditionParameter);
	void writeResult(Result result);
	void close() throws IOException;

}
