package lib.worker;

import jacusa.pileup.iterator.variant.ParallelDataValidator;

import java.util.Iterator;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.io.copytmp.CopyTmp;
import lib.location.CoordinateAdvancer;
import lib.location.StrandedCoordinateAdvancer;
import lib.location.UnstrandedCoordinateAdvancer;
import lib.tmp.CoordinateController;
import lib.util.AbstractTool;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public abstract class AbstractWorker<T extends AbstractData>
extends Thread
implements Iterator<ParallelData<T>> {

	public static enum STATUS {INIT, READY, FINISHED, BUSY, WAITING};

	private final WorkerDispatcher<T> workerDispatcher;
	private final ThreadIdContainer threadIdContainer;
	
	private final ConditionContainer<T> conditionContainer;
	private CoordinateController coordinateController;
	
	private final List<CopyTmp> copyTmps;
	
	private final ParallelDataValidator<T> parallelDataValidator;
	private ParallelData<T> parallelData;
	
	private STATUS status;
	
	public AbstractWorker(final WorkerDispatcher<T> workerDispatcher,
			final int threadId, List<CopyTmp> copyTmps, 
			final ParallelDataValidator<T> parallelDataValidator,
			final AbstractParameter<T> generalParameter) {
		this.workerDispatcher = workerDispatcher;
		threadIdContainer = new ThreadIdContainer(threadId);

		conditionContainer = new ConditionContainer<T>(generalParameter);
		coordinateController = new CoordinateController(generalParameter.getActiveWindowSize(), 
				createReferenceAdvancer(generalParameter.getConditionParameters()));

		this.copyTmps = copyTmps;
		this.parallelDataValidator = parallelDataValidator;
		status = STATUS.INIT;
	}

	@Override
	public boolean hasNext() {
		while (parallelData == null) {
			if (parallelDataValidator.isValid(parallelData)) {
				return true;
			}
		}
		/* TODO
		while () {
			// init 
			
			
			
			// build
			
			// TODO
		}
		*/
		return false;
	}
	
	@Override
	public ParallelData<T> next() {
		if (! hasNext()) {
			return null;
		}

		final Coordinate coordinate = new Coordinate(
				coordinateController.getReferenceAdvance().getCurrentCoordinate());
		coordinateController.advance();
		
		final ParallelData<T> parallelData = new ParallelData<T>(workerDispatcher.getMethodFactory());
		parallelData.setData(conditionContainer.getData(coordinate));
		parallelData.setCoordinate(coordinate);
		return parallelData;
	}
	
	protected abstract void doWork(final ParallelData<T> parallelData);

	protected void processWaiting() { 
		// overwrite
	}
	
	public void updateReservedWindowCoordinate(final Coordinate reservedWindowCoordinate) {
		AbstractTool.getLogger().addInfo("Thread " + (threadIdContainer.getThreadId() + 1) + ": " +
				"Working on contig " + 
				reservedWindowCoordinate.getContig() + ":" + 
				reservedWindowCoordinate.getStart() + "-" + 
				reservedWindowCoordinate.getEnd());
		
		coordinateController.updateReserved(reservedWindowCoordinate);
		
		final Coordinate activeWindowCoordinate = coordinateController.getActive();
		
		conditionContainer.update(activeWindowCoordinate, reservedWindowCoordinate);
	
	}

	protected void processInit() {
		// try to get a new reservedWindoCoordinate
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
		while (hasNext()) {
			ParallelData<T> parallelData = next();
			doWork(parallelData);	
		}
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

	/* TODo
	protected List<AbstractConditionParameter<T>> getConditionParamterers() {
		return workerDispatcher.getMethodFactory().getGeneralParameter().getConditionParameters();
	}
	
	protected int getActiveWindowSize() {
		return workerDispatcher.getMethodFactory().getGeneralParameter().getActiveWindowSize();
	}
	*/

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

	protected void setStatus(final STATUS status) {
		this.status = status;
	}

	protected CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	protected List<AbstractConditionParameter<T>> getConditionParameter() {
		return workerDispatcher.getMethodFactory().getParameter().getConditionParameters();
	}
	
	public ThreadIdContainer getThreadIdContainer() {
		return threadIdContainer;
	}

	public List<CopyTmp> getCopyTmps() {
		return copyTmps;
	}
	
	private CoordinateAdvancer createReferenceAdvancer(final List<AbstractConditionParameter<T>> conditionParameters) {
		final Coordinate coordinate = new Coordinate();
		for (final AbstractConditionParameter<T> conditionParameter : conditionParameters) {
			if (conditionParameter.getDataBuilderFactory().isStranded()) {
				coordinate.setStrand(STRAND.FORWARD);
				return new StrandedCoordinateAdvancer(coordinate);
			}
		}

		return new UnstrandedCoordinateAdvancer(coordinate);
	}
	
}
