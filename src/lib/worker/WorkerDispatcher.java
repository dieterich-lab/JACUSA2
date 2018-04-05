package lib.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.copytmp.CopyTmpExecuter;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.provider.CoordinateProvider;

/**
 * 
 * @author Michael Piechotta
 *
 * @param <T>
 */
public class WorkerDispatcher<T extends AbstractData, R extends Result<T>> {

	private final AbstractMethodFactory<T, R> methodFactory;
	private final CoordinateProvider coordinateProvider;

	private final List<AbstractWorker<T, ?>> workerContainer;
	private final List<AbstractWorker<T, R>> runningWorkers;

	private Integer comparisons;
	private List<Integer> threadIds;
	
	// private final ProgressIndicator progressIndicator;
	private int currentCoordinateIndex;
	
	public WorkerDispatcher(final AbstractMethodFactory<T, R> methodFactory) {
		this.methodFactory = methodFactory;
		this.coordinateProvider = methodFactory.getCoordinateProvider();
		
		final int maxThreads = methodFactory.getParameter().getMaxThreads();
		workerContainer = new ArrayList<AbstractWorker<T, ?>>(maxThreads);
		runningWorkers = new ArrayList<AbstractWorker<T, R>>(maxThreads);

		comparisons = 0;
		threadIds = new ArrayList<Integer>(10000);

		// progressIndicator = new ProgressIndicator(System.out);
		currentCoordinateIndex = 0;
	}

	protected synchronized AbstractWorker<T, R> createWorker() {
		return methodFactory.createWorker(workerContainer.size());
	}
	
	public synchronized Coordinate next() {
		currentCoordinateIndex++;
		
		Coordinate c = coordinateProvider.next();
		return c;
	}

	public synchronized boolean hasNext() {
		return coordinateProvider.hasNext();
	}

	public int run() throws IOException {
	    // final long startTime = System.currentTimeMillis();
	    // progressIndicator.print("Working:");

		while (hasNext() || ! runningWorkers.isEmpty()) {
			for (int i = 0; i < runningWorkers.size(); ++i) {
				final AbstractWorker<T, R> runningWorker = runningWorkers.get(i);
				
				switch (runningWorker.getStatus()) {
				case FINISHED:
					synchronized (comparisons) {
						comparisons += runningWorker.getComparisons();
					}
					synchronized (runningWorkers) {
						runningWorkers.remove(runningWorker);
					}
					break;

				default:
					break;
				}
			} 

			synchronized (this) {
				// fill thread container
				while (runningWorkers.size() < methodFactory.getParameter().getMaxThreads() && hasNext()) {
					final AbstractWorker<T, R> worker = createWorker();

					workerContainer.add(worker);
					runningWorkers.add(worker);
					worker.start();
				}

				/*
				if (! getMethodFactory().getParameter().isDebug()) {
					// progressIndicator.update("Progress: ", startTime, currentCoordinateIndex, coordinateProvider.getTotal());
				}
				
				// computation finished
				if (! hasNext() && runningWorkers.isEmpty()) {
					// progressIndicator.print("\nDone!\n");
					break;
				}*/
				try {
					this.wait(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		writeOutput();

		return comparisons;
	}

	protected List<AbstractWorker<T, ?>> getWorkerContainer() {
		return workerContainer;
	}

	protected List<Integer> getThreadIds() {
		return threadIds;
	}
	
	protected AbstractMethodFactory<T, R> getMethodFactory() {
		return methodFactory;
	}
	
	protected void writeOutput() throws IOException {
		getMethodFactory().getParameter()
			.getResultWriter().writeHeader(getMethodFactory().getParameter().getConditionParameters());
		
		// progressIndicator.print("Merging tmp files:");
		AbstractTool.getLogger().addInfo("Started merging tmp files...");
		final CopyTmpExecuter<T> copyTmpExecuter = new CopyTmpExecuter<T>(threadIds, workerContainer);
		copyTmpExecuter.copy();
		// progressIndicator.print("\nDone!");
		AbstractTool.getLogger().addInfo("Finished merging tmp files!");

		// close output
		getMethodFactory().getParameter().getResultWriter().close();
		if (getMethodFactory().getParameter().getFilteredResultWriter() != null) {
			getMethodFactory().getParameter().getFilteredResultWriter().close();
		}
	}

}