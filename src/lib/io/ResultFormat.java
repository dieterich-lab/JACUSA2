package lib.io;

import lib.io.copytmp.CopyTmpResult;
import lib.worker.WorkerDispatcher;

/**
 * Interface 
 *
 * @param <T>
 * @param <R>
 */
public interface ResultFormat {

	/**
	 * TODO add comments.
	 * 
	 * @return unique char 
	 */
	char getID();
	
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
	ResultWriter createWriter(String filename);
	
	/**
	 * TOOD add comments.
	 * 
	 * @param threadId
	 * @return
	 */
	CopyTmpResult createCopyTmp(int threadId, WorkerDispatcher workerDispatcher);

}
