package lib.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpExecuter;
import lib.method.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;

/**
 * 
 * @author Michael Piechotta
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
	
	// private final ProgressIndicator progressIndicator;
	// private int currentCoordinateIndex;
	
	private ResultWriter resultWriter;
	private ResultWriter filteredResultWriter;
	
	public WorkerDispatcher(final AbstractMethod methodFactory) {
		this.methodFactory = methodFactory;
		
		final AbstractParameter parameter = methodFactory.getParameter();
		final int maxThreads = parameter.getMaxThreads();
		workerContainer = new ArrayList<AbstractWorker>(maxThreads);
		runningWorkers = new ArrayList<AbstractWorker>(maxThreads);

		comparisons = 0;
		threadIds = new ArrayList<Integer>(10000);

		// progressIndicator = new ProgressIndicator(System.out);
		// currentCoordinateIndex = 0;
		
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
		// currentCoordinateIndex++;
		
		Coordinate c = methodFactory.getCoordinateProvider().next();
		return c;
	}

	public synchronized boolean hasNext() {
		return methodFactory.getCoordinateProvider().hasNext();
	}

	public int run() throws IOException {
	    // final long startTime = System.currentTimeMillis();
	    // progressIndicator.print("Working:");

		while (hasNext() || ! runningWorkers.isEmpty()) {
			for (int i = 0; i < runningWorkers.size(); ++i) {
				final AbstractWorker runningWorker = runningWorkers.get(i);
				
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
					final AbstractWorker worker = createWorker();

					workerContainer.add(worker);
					runningWorkers.add(worker);
					worker.start();
				}

				/*
				if (! getMethodFactory().getParameter().isDebug()) {
					// progressIndicator.update("Progress: ", startTime, currentCoordinateIndex, coordinateProvider.getTotal());
				}
				*/
				
				// computation finished
				if (! hasNext() && runningWorkers.isEmpty()) {
					// progressIndicator.print("\nDone!\n");
					break;
				}
				try {
					this.wait(60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
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
	
	private List<AbstractConditionParameter> getConditionParameters(){
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

		// progressIndicator.print("Merging tmp files:");
		AbstractTool.getLogger().addInfo("Started merging tmp files...");
		final CopyTmpExecuter copyTmpExecuter = new CopyTmpExecuter(threadIds, workerContainer);
		copyTmpExecuter.copy();
		// progressIndicator.print("\nDone!");
		AbstractTool.getLogger().addInfo("Finished merging tmp files!");

		// close output
		if (resultWriter != null) {
			try {
				resultWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			resultWriter = null;
		}
		
		if (filteredResultWriter != null) {
			try {
				filteredResultWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			filteredResultWriter = null;
		}
	}

}