package jacusa.pileup.dispatcher;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.AbstractData;
import jacusa.io.Output;
import jacusa.io.OutputWriter;
import jacusa.pileup.worker.AbstractWorker;
import jacusa.util.Coordinate;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author Michael Piechotta
 *
 * @param <T>
 */
public abstract class AbstractWorkerDispatcher<T extends AbstractData> {

	private final CoordinateProvider coordinateProvider;
	private final AbstractParameters<T> parameters;

	private final List<AbstractWorker<T>> workerContainer;
	private final List<AbstractWorker<T>> runningWorkers;

	private Integer comparisons;
	private List<Integer> threadIds;
	
	private String[][] pathnames;
	
	public AbstractWorkerDispatcher(
			final CoordinateProvider coordinateProvider, 
			final AbstractParameters<T> parameters) {
		this.coordinateProvider = coordinateProvider;
		
		this.pathnames = new String[parameters.getConditions()][];
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions(); conditionIndex++) {
			final String[] pathnames = parameters.getConditionParameters(conditionIndex).getPathnames();
			
			this.pathnames[conditionIndex] = new String[pathnames.length];
			System.arraycopy(
					parameters.getConditionParameters(conditionIndex).getPathnames(), 
					0, 
					this.pathnames[conditionIndex], 
					0, 
					pathnames.length);
			
		}
		
		workerContainer = new ArrayList<AbstractWorker<T>>(parameters.getMaxThreads());
		runningWorkers	= new ArrayList<AbstractWorker<T>>(parameters.getMaxThreads());
		comparisons 	= 0;
		threadIds		= new ArrayList<Integer>(10000);
		
		this.parameters = parameters;
	}

	protected abstract AbstractWorker<T> buildNextWorker();

	public synchronized Coordinate next() {
		return coordinateProvider.next();
	}

	public synchronized boolean hasNext() {
		return coordinateProvider.hasNext();
	}

	public int run() {
		// write Header
		try {
			String header = parameters
					.getFormat().getHeader(parameters.getConditionParameters());
			if (header != null) {
				parameters.getOutput().write(header);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (hasNext() || ! runningWorkers.isEmpty()) {
			for (int i = 0; i < runningWorkers.size(); ++i) {
				AbstractWorker<T> runningWorker = runningWorkers.get(i);
				
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
				while (runningWorkers.size() < parameters.getMaxThreads() && hasNext()) {
					AbstractWorker<T> worker = buildNextWorker();
					
					workerContainer.add(worker);
					runningWorkers.add(worker);
					worker.start();
				}

				// computation finished
				if (! hasNext() && runningWorkers.isEmpty()) {
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
	
	public AbstractParameters<T> getParameters() {
		return parameters;
	}

}