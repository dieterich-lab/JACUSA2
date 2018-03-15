package lib.io;

import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.copytmp.CopyTmpResult;

/**
 * Interface 
 *
 * @param <T>
 * @param <R>
 */
public interface ResultFormat<T extends AbstractData, R extends Result<T>> {

	/**
	 * TODO add comments.
	 * 
	 * @return unique char 
	 */
	char getC();
	
	/**
	 * TODO add comments.
	 * 
	 * @return string description
	 */
	String getDesc();

	/**
	 * TODO add comments.
	 * 
	 * @param filename
	 * @return
	 */
	ResultWriter<T, R> createWriter(String filename);
	
	/**
	 * TOOD add comments.
	 * 
	 * @param threadId
	 * @return
	 */
	CopyTmpResult<T, R> createCopyTmp(int threadId);
}
