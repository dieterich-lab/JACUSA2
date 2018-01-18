package lib.io.copytmp;

import java.io.IOException;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;

public interface CopyTmpResult<T extends AbstractData, R extends Result<T>> {

	void newIteration();
	
	void addResult(final R result, final List<AbstractConditionParameter<T>> conditionParameters) throws Exception;
	void copy(final int iteration) throws IOException;

	void closeTmpReader() throws IOException;
	void closeTmpWriter() throws IOException;

}
