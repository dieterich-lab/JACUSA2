package lib.worker;

import jacusa.filter.AbstractFilter;

import java.util.Iterator;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.result.Result;
import lib.data.validator.CompositeParallelDataValidator;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.util.coordinate.CoordinateController;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;


public abstract class AbstractWorker<T extends AbstractData, R extends Result<T>>
extends Thread
implements Iterator<ParallelData<T>> {

	public static enum STATUS {INIT, READY, FINISHED, BUSY, WAITING};

	private final WorkerDispatcher<T, R> workerDispatcher;
	private final ThreadIdContainer threadIdContainer;
	private final CopyTmpResult<T, R> copyTmpResult;
	
	private final ConditionContainer<T> conditionContainer;
	private CoordinateController coordinateController;
	
	private final ParallelDataValidator<T> parallelDataValidator;
	private ParallelData<T> parallelData;
	
	private int comparisons;
	private STATUS status;
	
	public AbstractWorker(
			final ReferenceSetter<T> referenceSetter,
			final WorkerDispatcher<T, R> workerDispatcher,
			final int threadId,
			final CopyTmpResult<T, R> copyTmpResult,
			final List<ParallelDataValidator<T>> parallelDataValidators,
			final AbstractParameter<T, R> parameter) {
		this.workerDispatcher = workerDispatcher;
		threadIdContainer = new ThreadIdContainer(threadId);
		this.copyTmpResult = copyTmpResult;

		conditionContainer = new ConditionContainer<T>(parameter);
		coordinateController = new CoordinateController(conditionContainer);
		conditionContainer.initReplicateContainer(referenceSetter, coordinateController, parameter);

		this.parallelDataValidator = new CompositeParallelDataValidator<T>(parallelDataValidators);

		comparisons = 0;
		status = STATUS.INIT;
		setName("JACUSA Worker " + threadId);
	}

	protected boolean filter(final R result) {
		boolean isFiltered = false;
		// apply each filter
		for (final AbstractFilter<T> filter : conditionContainer.getFilterContainer().getFilters()) {
			if (filter.applyFilter(result)) {
				isFiltered = true;
			}
		}

		result.setFiltered(isFiltered);
		return isFiltered;
	}
	
	@Override
	public boolean hasNext() {
		while (coordinateController.checkCoordinateAdvancerWithinActiveWindow()) {
			final Coordinate coordinate = new Coordinate(coordinateController.getCoordinateAdvancer().getCurrentCoordinate());
			final T[][] data = conditionContainer.getData(coordinate);
			parallelData = new ParallelData<T>(workerDispatcher.getMethodFactory().getDataGenerator(), data);

			if (parallelData != null && parallelDataValidator.isValid(parallelData)) {
				comparisons++;
				return true;
			}
			coordinateController.advance();
		}
		
		if (coordinateController.hasNext()) {
			final Coordinate activeWindowCoordinate = coordinateController.next();
			conditionContainer.updateActiveWindowCoordinates(activeWindowCoordinate);
			return hasNext();
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

	public void doWork(final ParallelData<T> parallelData) {
		R result = process(parallelData);
		if (result == null) {
			return;
		}

		final AbstractParameter<T, R> parameter = workerDispatcher.getMethodFactory().getParameter();
		if (parameter.getFilterConfig().hasFiters()) {
			filter(result);
		}

		try {
			copyTmpResult.addResult(result, parameter.getConditionParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract R process(final ParallelData<T> parallelData);

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
		copyTmpResult.newIteration();
		ParallelData<T> parallelData;
		while ((parallelData = next()) != null) {
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

	public CopyTmpResult<T, R> getCopyTmpResult() {
		return copyTmpResult;
	}
	
	public int getComparisons() {
		return comparisons;
	}
	
}
