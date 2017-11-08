package lib.worker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.data.AbstractData;
import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;
import lib.util.ProgressIndicator;
import lib.util.coordinateprovider.CoordinateProvider;

/**
 * 
 * @author Michael Piechotta
 *
 * @param <T>
 */
public abstract class AbstractWorkerDispatcher<T extends AbstractData> {

	private final AbstractMethodFactory<T> methodFactory;
	private final CoordinateProvider coordinateProvider;

	private final List<AbstractWorker<T>> workerContainer;
	private final List<AbstractWorker<T>> runningWorkers;

	private Integer comparisons;
	private List<Integer> threadIds;
	
	private ProgressIndicator progressIndicator;
	private int currentCoordinateIndex;
	
	public AbstractWorkerDispatcher(final AbstractMethodFactory<T> methodFactory) {
		this.methodFactory = methodFactory;
		this.coordinateProvider = methodFactory.getCoordinateProvider();
		
		final int maxThreads = methodFactory.getParameters().getMaxThreads();
		workerContainer = new ArrayList<AbstractWorker<T>>(maxThreads);
		runningWorkers = new ArrayList<AbstractWorker<T>>(maxThreads);

		comparisons = 0;
		threadIds = new ArrayList<Integer>(10000);

		progressIndicator = new ProgressIndicator(System.out);
		currentCoordinateIndex = 0;
	}

	protected AbstractWorker<T> createWorker() throws IOException {
		return methodFactory.createWorker();
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
				while (runningWorkers.size() < methodFactory.getParameters().getMaxThreads() && hasNext()) {
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

	/**
	 * 
	 * @return
	 */

	public List<AbstractWorker<T>> getWorkerContainer() {
		return workerContainer;
	}

	public List<Integer> getThreadIds() {
		return threadIds;
	}
	
	public AbstractMethodFactory<T> getMethodFactory() {
		return methodFactory;
	}
	
	protected void writeOutput() throws IOException {
		progressIndicator.print("Merging files:");
		/* TODO CopyTmpRecords<T> copyTmp = new CopyTmpRecords<T>(threadIds, methodFactory.getParameters(), workerContainer);
		copyTmp.copy();
		copyTmp.close();
		*/
		progressIndicator.print("\nDone!");
	}
	
	/* TODO
	protected void writeOutput() {
		Output filteredOutput = null;
		if (parameters.isSeparate()) {
			final String filename = parameters.getOutput().getInfo().concat(".filtered");
			final File file = new File(filename);
			try {
				filteredOutput = new OutputWriter(file);
				filteredOutput.write(parameters.getFormat().getHeader(null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		BufferedReader[] brs = new BufferedReader[parameters.getMaxThreads()];
		for (int threadId = 0; threadId < brs.length; ++threadId) {
			String filename = parameters.getOutput().getInfo() + "_" + threadId + "_tmp.gz";
			final File file = new File(filename);

			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				GZIPInputStream gzip = new GZIPInputStream(fileInputStream);
				brs[threadId] = new BufferedReader(new InputStreamReader(gzip));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (int threadId : threadIds) {
			final BufferedReader br = brs[threadId];
			try {
				String line;
				while((line = br.readLine()) != null && ! line.startsWith("##")) {
					final int i = line.length() - 1;
					final char c = line.charAt(i);
					if (parameters.isSeparate() == false || c == 'F') {
						parameters.getOutput().write(line.substring(0, i));
					} else {
						filteredOutput.write(line.substring(0, i));
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (BufferedReader br : brs) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (int threadId = 0; threadId < parameters.getMaxThreads(); ++threadId) {
			String filename = parameters.getOutput().getInfo() + "_" + threadId + "_tmp.gz";
			new File(filename).delete();
		}
		
		try {
			parameters.getOutput().close();
			if (filteredOutput != null) {
				filteredOutput.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

}