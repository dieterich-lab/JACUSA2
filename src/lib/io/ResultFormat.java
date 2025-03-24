package lib.io;

import lib.io.copytmp.CopyTmpResult;
import lib.worker.WorkerDispatcher;

public interface ResultFormat {

	/**
	 * Return the unique ID for the file format.
	 * 
	 * @return unique char 
	 */
	char getID();
	
	/**
	 * Returns a short description of the file format.
	 * 
	 * @return string description
	 */
	String getDesc();

	/**
	 * Creates a ResultWriter for this file format.
	 * 
	 * @param filename
	 * @return
	 */
	ResultWriter createWriter(String filename);
	
	/**
	 * Creates an Object to handle temporary data for each thread. 
	 * 
	 * @param threadId
	 * @return
	 */
	CopyTmpResult createCopyTmp(int threadId, WorkerDispatcher workerDispatcher);
	
}
