package lib.worker;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.data.AbstractData;
import lib.io.copytmp.CopyTmp;
import lib.tmp.CoordinateController;
import lib.util.Coordinate;

public abstract class AbstractWorker<T extends AbstractData> 
extends Thread {

	public static enum STATUS {INIT, READY, FINISHED, BUSY, WAITING};

	private final WorkerDispatcher<T> workerDispatcher;
	private final ThreadIdContainer threadIdContainer;
	
	private final List<CopyTmp> copyTmps;
	private STATUS status;

	private CoordinateController coordinateController;
	
	// TODO private final List<OverlappingRecordWrapperContainer> windowContainers;
	
	public AbstractWorker(final WorkerDispatcher<T> workerDispatcher,
			final int threadId, List<CopyTmp> copyTmps, 
			final AbstractParameters<T> parameters) {
		this.workerDispatcher = workerDispatcher;
		threadIdContainer = new ThreadIdContainer(threadId);

		this.copyTmps = copyTmps;
		status = STATUS.INIT;
	}

	protected abstract void doWork();
	
	protected void processWaiting() {
		// TODO
	}
	
	public void updateReservedWindowCoordinate(final Coordinate reservedWindowCoordinate) {
		coordinateController = new CoordinateController(reservedWindowCoordinate, getActiveWindowSize());
	}

	protected void processInit() {
		Coordinate reserverdWindowCoordinate = null;
		synchronized (workerDispatcher) {
			if (workerDispatcher.hasNext()) {
				workerDispatcher.getThreadIds().add(threadIdContainer.getThreadId());
				reserverdWindowCoordinate = workerDispatcher.next();
			}
		}
		synchronized (this) {
			if (reserverdWindowCoordinate == null) {
				setStatus(STATUS.FINISHED);
			} else {
				updateReservedWindowCoordinate(reserverdWindowCoordinate);
				setStatus(STATUS.READY);
			}
		}
	}
	
	protected void processReady() {
		status = STATUS.BUSY;
		doWork();
		 // TODO what status
		status = STATUS.INIT;
	}

	@Override
	public final void run() {
		while (status != STATUS.FINISHED) {
			switch (status) {

			case WAITING:
				processWaiting();
				break;
			
			case READY:
				synchronized (this) {
					processReady();
				}
				break;
				
			case INIT:
				processInit();
				break;

			
			default:
				break;
			}
		}

		/* TODO close reader and open writer
		tmpWriter.close();
		try {
			tmpReader = new TmpWorkerReader<T>(tmpWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		synchronized (workerDispatcher) {
			workerDispatcher.notify();
		}

	}

	protected List<AbstractConditionParameter<T>> getConditionParamterers() {
		return workerDispatcher.getMethodFactory().getParameters().getConditionParameters();
	}
	
	protected int getActiveWindowSize() {
		return workerDispatcher.getMethodFactory().getParameters().getActiveWindowSize();
	}

	/* TODO
	protected synchronized void processParallelDataIterator(final WindowedIterator<T> parallelDataIterator) {
		// print informative log
		AbstractTool.getLogger().addInfo("Started screening contig " + 
				parallelDataIterator.getWindow().getContig() + 
				":" + 
				parallelDataIterator.getWindow().getStart() + 
				"-" + 
				parallelDataIterator.getWindow().getEnd());
		
		// iterate over parallel pileups
		while (parallelDataIterator.hasNext()) {
			final ParallelPileupData<T> parallelPileup = parallelDataIterator.next();
			final Result<T> result = processParallelData(parallelPileup, parallelDataIterator);

			// considered comparisons

			if (result == null) {
				continue;
			}

			/* TODO
			final String line = parameters.getFormat().convert2String(result);
			try {
				char c = 'F';
				if (! result.getFilterInfo().isEmpty()) {
					c = 'T';
				}
				final String s = new String(line + c + "\n"); 
				zip.write(s.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	*/

	public STATUS getStatus() {
		return status;
	}

	protected void setStatus(STATUS status) {
		this.status = status;
	}
	
	public ThreadIdContainer getThreadIdContainer() {
		return threadIdContainer;
	}

	public List<CopyTmp> getCopyTmps() {
		return copyTmps;
	}

}
