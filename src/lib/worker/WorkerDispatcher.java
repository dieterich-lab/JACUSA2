package lib.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.data.AbstractData;
import lib.io.copytmp.CopyTmpExecuter;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.util.Coordinate;
import lib.util.ProgressIndicator;
import lib.util.coordinateprovider.CoordinateProvider;

/**
 * 
 * @author Michael Piechotta
 *
 * @param <T>
 */
public class WorkerDispatcher<T extends AbstractData> {

	private final AbstractMethodFactory<T> methodFactory;
	private final CoordinateProvider coordinateProvider;

	private final List<AbstractWorker<T>> workerContainer;
	private final List<AbstractWorker<T>> runningWorkers;

	private Integer comparisons;
	private List<Integer> threadIds;
	
	private ProgressIndicator progressIndicator;
	private int currentCoordinateIndex;
	
	public WorkerDispatcher(final AbstractMethodFactory<T> methodFactory) {
		this.methodFactory = methodFactory;
		this.coordinateProvider = methodFactory.getCoordinateProvider();
		
		final int maxThreads = methodFactory.getParameter().getMaxThreads();
		workerContainer = new ArrayList<AbstractWorker<T>>(maxThreads);
		runningWorkers = new ArrayList<AbstractWorker<T>>(maxThreads);

		comparisons = 0;
		threadIds = new ArrayList<Integer>(10000);

		progressIndicator = new ProgressIndicator(System.out);
		currentCoordinateIndex = 0;
	}

	protected AbstractWorker<T> createWorker() {
		return methodFactory.createWorker(this);
	}
	
	public synchronized Coordinate next() {
		currentCoordinateIndex++;
		return coordinateProvider.next();
	}

	public synchronized boolean hasNext() {
		return coordinateProvider.hasNext();
	}

	public int run() throws IOException {
	    final long startTime = System.currentTimeMillis();
	    progressIndicator.print("Implanting variants:");

		while (hasNext() || ! runningWorkers.isEmpty()) {
			for (int i = 0; i < runningWorkers.size(); ++i) {
				final AbstractWorker<T> runningWorker = runningWorkers.get(i);
				
				switch (runningWorker.getStatus()) {
				case FINISHED:
					synchronized (comparisons) {
						// TODO comparisons += runningWorker.getSites();
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
					final AbstractWorker<T> worker = createWorker();

					workerContainer.add(worker);
					runningWorkers.add(worker);
					worker.start();
				}

				progressIndicator.update("Progress: ", startTime, currentCoordinateIndex, coordinateProvider.getTotal());
				
				// computation finished
				if (! hasNext() && runningWorkers.isEmpty()) {
					progressIndicator.print("\nDone!\n");
					break;
				}
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

	protected List<AbstractWorker<T>> getWorkerContainer() {
		return workerContainer;
	}

	protected List<Integer> getThreadIds() {
		return threadIds;
	}
	
	protected AbstractMethodFactory<T> getMethodFactory() {
		return methodFactory;
	}
	
	protected void writeOutput() throws IOException {
		progressIndicator.print("Merging tmp files:");
		AbstractTool.getLogger().addInfo("Started merging tmp files...");
		final CopyTmpExecuter<T> copyTmpExecuter = new CopyTmpExecuter<T>(threadIds, workerContainer);
		copyTmpExecuter.copy();
		progressIndicator.print("\nDone!");
		AbstractTool.getLogger().addInfo("Finished merging tmp files!");
	}

}