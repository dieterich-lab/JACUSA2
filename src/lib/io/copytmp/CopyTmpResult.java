package lib.io.copytmp;

import java.io.IOException;

import lib.data.result.Result;

/**
 * Defines interface to store results in blocks that can be identified by iterations.
 */
public interface CopyTmpResult {

	/**
	 * Start a new iteration of adding results.
	 */
	void newIteration();
	
	/**
	 * Adds a result to tmp.
	 * @param result to be added to tmp
	 * @throws Exception
	 */
	void addResult(final Result result) throws Exception;
	/**
	 * Copies result for iteration.
	 * @param iteration to be used for copying.
	 * @throws IOException
	 */
	void copy(final int iteration) throws IOException;

	/*
	 * IO related
	 */
	void closeTmpReader() throws IOException;
	void closeTmpWriter() throws IOException;

}
