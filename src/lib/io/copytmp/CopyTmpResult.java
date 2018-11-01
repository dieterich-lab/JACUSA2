package lib.io.copytmp;

import java.io.IOException;

import lib.data.result.Result;

public interface CopyTmpResult {

	void newIteration();
	
	void addResult(final Result result) throws Exception;
	void copy(final int iteration) throws IOException;

	void closeTmpReader() throws IOException;
	void closeTmpWriter() throws IOException;

}
