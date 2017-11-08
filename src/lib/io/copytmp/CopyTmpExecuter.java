package lib.io.copytmp;


import java.io.IOException;
import java.util.List;

import lib.data.AbstractData;
import lib.util.AbstractTool;
import lib.worker.AbstractWorker;

public class CopyTmpExecuter<T extends AbstractData> {

	private final String msg; 
	private final List<Integer> threadIds;
	private final List<AbstractWorker<T>> workerContainer;
	
	public CopyTmpExecuter(final String msg, 
			final List<Integer> threadIds, 
			final List<AbstractWorker<T>> workerContainer) {
		this.msg = msg;
		this.threadIds = threadIds;
		this.workerContainer = workerContainer;
	}

	public void copy() throws IOException {
		AbstractTool.getLogger().addInfo(msg);
		int iteration = 0;
		for (int threadId : threadIds) {
			// current worker
			final AbstractWorker<T> worker = workerContainer.get(threadId);
			for (final CopyTmp copyTmp : worker.getCopyTmps()) {
				copyTmp.copy(iteration);
			}
			++iteration;
		}
	}

}
