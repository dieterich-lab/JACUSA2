package lib.io.copytmp;

import java.io.IOException;
import java.util.List;

import lib.data.AbstractData;
import lib.worker.AbstractWorker;

public class CopyTmpExecuter<T extends AbstractData> {

	private final List<Integer> threadIds;
	private final List<AbstractWorker<T>> workerContainer;
	
	public CopyTmpExecuter(
			final List<Integer> threadIds, 
			final List<AbstractWorker<T>> workerContainer) {
		this.threadIds = threadIds;
		this.workerContainer = workerContainer;
	}

	public void copy() throws IOException {
		for (int iteration = 0; iteration < threadIds.size(); iteration++) {
			final int threadId = threadIds.get(iteration);
			// current worker
			final AbstractWorker<T> worker = workerContainer.get(threadId);
			for (final CopyTmp copyTmp : worker.getCopyTmps()) {
				copyTmp.copy(iteration);
			}
		}
	}

}
