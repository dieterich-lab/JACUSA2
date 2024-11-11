package lib.io;

import jacusa.io.format.ParallelToString;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.WorkerDispatcher;

import java.util.List;

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

	//methods necessary for output-format X
	void processCLI(final String line);
	List<ParallelToString> getAvailable();
	List<ParallelToString> getSelected();

}
