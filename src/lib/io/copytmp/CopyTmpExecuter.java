package lib.io.copytmp;

import java.io.IOException;
import java.util.List;

import lib.worker.AbstractWorker;

/**
 * DOCUMENT
 */
public class CopyTmpExecuter {

	private final List<Integer> threadIds;
	private final List<AbstractWorker> workerContainer;
	
	public CopyTmpExecuter(
			final List<Integer> threadIds, 
			final List<AbstractWorker> workerContainer) {
		this.threadIds 			= threadIds;
		this.workerContainer 	= workerContainer;
	}

	public void copy() throws IOException {
		final int[] iteration = new int[workerContainer.size()];

		// close writer
		for (final AbstractWorker worker : workerContainer) {
			worker.getCopyTmpResult().closeTmpWriter();
		}
		
		for (final int threadId : threadIds) {
			// current worker
			final AbstractWorker worker = workerContainer.get(threadId);
			worker.getCopyTmpResult().copy(iteration[threadId]);
			iteration[threadId]++;
		}

		// close reader
		for (final AbstractWorker worker : workerContainer) {
			worker.getCopyTmpResult().closeTmpReader();
		}
	}

}
