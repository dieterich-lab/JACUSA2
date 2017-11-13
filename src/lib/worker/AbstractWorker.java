package lib.worker;

import jacusa.data.validator.CompositeParallelDataValidator;
import jacusa.data.validator.ParallelDataValidator;

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
	
	private final ParallelDataValidator<T> parallelDataValidator;
	private ParallelData<T> parallelData;
	
	private STATUS status;
	
	public AbstractWorker(final WorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final List<ParallelDataValidator<T>> parallelDataValidators,
			final AbstractParameter<T> generalParameter) {
		this.workerDispatcher = workerDispatcher;
		threadIdContainer = new ThreadIdContainer(threadId);

		conditionContainer = new ConditionContainer<T>(generalParameter);
		coordinateController = new CoordinateController(generalParameter.getActiveWindowSize(), 
				createReferenceAdvancer(generalParameter.getConditionParameters()));

		this.parallelDataValidator = new CompositeParallelDataValidator<T>(parallelDataValidators);
		status = STATUS.INIT;
	}

	// TODO make faster if replicates are not valid 
	@Override
	public boolean hasNext() {
		while (coordinateController.checkReferenceAdvancerWithinActiveWindow()) {
			final Coordinate coordinate = new Coordinate(coordinateController.getReferenceAdvance().getCurrentCoordinate());
			final T[][] data = conditionContainer.getData(coordinate);
			parallelData = new ParallelData<T>(workerDispatcher.getMethodFactory(), coordinate, data);
			do {
				if (parallelData != null && parallelDataValidator.isValid(parallelData)) {
					return true;
				}
			} while (coordinateController.advance());
			if (coordinateController.hasNext()) {
				final Coordinate activeWindowCoordinate = coordinateController.next();
				conditionContainer.updateActiveWindowCoordinates(activeWindowCoordinate);
			}
		}

		return false;
	}
	
	@Override
	public ParallelData<T> next() {
		if (! hasNext()) {
			return null;
		}

		final ParallelData<T> parallelData = new ParallelData<T>(this.parallelData);
		coordinateController.advance();
		
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
		conditionContainer.updateWindowCoordinates(coordinateController.next(), reservedWindowCoordinate);
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
		for (final CopyTmp copyTmp : getCopyTmps()) {
			copyTmp.nextIteration();
		}
		while (hasNext()) {
			final ParallelData<T> parallelData = next();
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

	protected ConditionContainer<T> getConditionContainer() {
		return conditionContainer;
	}
	
	public ThreadIdContainer getThreadIdContainer() {
		return threadIdContainer;
	}

	public abstract List<CopyTmp> getCopyTmps();
	
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
