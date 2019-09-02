package lib.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpExecuter;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker.STATUS;

/**
 * 
 * 
 *
 * @param <T>
 */
public class WorkerDispatcher {

	public static final String FILE_SUFFIX = ".filtered";
	
	private final AbstractMethod methodFactory;

	private final List<AbstractWorker> workerContainer;
	private final List<AbstractWorker> runningWorkers;

	private Integer comparisons;
	private List<Integer> threadIds;

	private ResultWriter resultWriter;
	private ResultWriter filteredResultWriter;

	public WorkerDispatcher(final AbstractMethod methodFactory) {
		this.methodFactory = methodFactory;
		
		final GeneralParameter parameter = methodFactory.getParameter();
		final int maxThreads = parameter.getMaxThreads();
		workerContainer = new ArrayList<>(maxThreads);
		runningWorkers = new ArrayList<>(maxThreads);

		comparisons = 0;
		threadIds = new ArrayList<>(10000);

		resultWriter = parameter.getResultFormat().createWriter(parameter.getResultFilename());
		if (parameter.splitFiltered()) {
			filteredResultWriter = parameter.getResultFormat().createWriter(parameter.getResultFilename() + FILE_SUFFIX);
		} else {
			filteredResultWriter = resultWriter; 
		}
	}

	private synchronized AbstractWorker createWorker() {
		return methodFactory.createWorker(workerContainer.size());
	}
	
	public synchronized Coordinate next() {
		return methodFactory.getCoordinateProvider().next();
	}

	public synchronized boolean hasNext() {
		return methodFactory.getCoordinateProvider().hasNext();
	}

	public int run() throws IOException {
		while (hasNext() || ! runningWorkers.isEmpty()) {
			for (int i = 0; i < runningWorkers.size(); ++i) {
				final AbstractWorker runningWorker = runningWorkers.get(i);
				if (runningWorker.getStatus() == STATUS.FINISHED) {
					comparisons += runningWorker.getComparisons();
					synchronized (runningWorkers) {
						runningWorkers.remove(runningWorker);
					}
				}
			}

			synchronized (this) {
				// fill thread container
				while (runningWorkers.size() < methodFactory.getParameter().getMaxThreads() && hasNext()) {
					final AbstractWorker worker = createWorker();

					workerContainer.add(worker);
					runningWorkers.add(worker);
					worker.start();
				}

				// computation finished
				if (! hasNext() && runningWorkers.isEmpty()) {
					break;
				}
				try {
					this.wait(60 * (long)1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		writeOutput();

		return comparisons;
	}

	public ResultWriter getResultWriter() {
		return resultWriter;
	}
	
	public ResultWriter getFilteredResultWriter() {
		return filteredResultWriter;
	}
	
	public List<Integer> getThreadIds() {
		return threadIds;
	}
	
	private List<ConditionParameter> getConditionParameters(){
		return methodFactory.getParameter().getConditionParameters();
	}
	
	private boolean splitFiltered() {
		return methodFactory.getParameter().splitFiltered();
	}
	
	private void writeOutput() throws IOException {
		resultWriter.writeHeader(getConditionParameters());
		// add header to filtered file
		if (splitFiltered()) {
			filteredResultWriter.writeHeader(getConditionParameters());
		}

		AbstractTool.getLogger().addInfo("Started merging tmp files...");
		final CopyTmpExecuter copyTmpExecuter = new CopyTmpExecuter(threadIds, workerContainer);
		copyTmpExecuter.copy();
		AbstractTool.getLogger().addInfo("Finished merging tmp files!");

		// close output
		if (resultWriter != null) {
			try {
				resultWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			resultWriter = null;
		}
		
		if (filteredResultWriter != null) {
			try {
				filteredResultWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			filteredResultWriter = null;
		}
	}

}