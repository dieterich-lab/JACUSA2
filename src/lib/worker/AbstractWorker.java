package lib.worker;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import jacusa.filter.Filter;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.assembler.ConditionContainer;
import lib.data.assembler.ReplicateContainer;
import lib.data.cache.container.ComplexSharedCache;
import lib.data.cache.container.FileReferenceProvider;
import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SharedCache;
import lib.data.cache.container.SimpleMDReferenceProvider;
import lib.data.result.Result;
import lib.data.validator.paralleldata.CompositeParallelDataValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.method.AbstractMethod;
import lib.util.coordinate.CoordinateController;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;

public abstract class AbstractWorker
extends Thread
implements Iterator<ParallelData> {

	public static enum STATUS {INIT, READY, FINISHED, BUSY, WAITING};

	private final AbstractMethod method;
	
	private final ThreadIdContainer threadIdContainer;
	private final CopyTmpResult copyTmpResult;
	
	private final ConditionContainer conditionContainer;
	private CoordinateController coordinateController;

	private final ParallelDataValidator parallelDataValidator;
	
	private int comparisons;
	private STATUS status;
	
	private ParallelData parallelData;
	
	public AbstractWorker(final AbstractMethod method, final int threadId) {
		this.method = method;
		
		threadIdContainer = new ThreadIdContainer(threadId);

		copyTmpResult = getParameter().getResultFormat().createCopyTmp(threadId, method.getWorkerDispatcherInstance());
		
		conditionContainer = new ConditionContainer(getParameter());
		coordinateController = new CoordinateController(getParameter().getActiveWindowSize(), conditionContainer);

		final ReferenceProvider referenceProvider = createReferenceProvider(coordinateController);
		final SharedCache sharedCache = new ComplexSharedCache(referenceProvider);
		conditionContainer.initReplicateContainer(sharedCache, getParameter(), method);

		parallelDataValidator = new CompositeParallelDataValidator(method.createParallelDataValidators());

		comparisons = 0;
		status = STATUS.INIT;
		setName(AbstractTool.getLogger().getTool().getName() + " Worker " + threadId);
	}

	private ReferenceProvider createReferenceProvider(final CoordinateController coordinateController) {
		final IndexedFastaSequenceFile referencefile = 
				getParameter().getReferenceFile();
		if (referencefile == null) {
			final List<String> recordFilenames =
					getConditionParameter().stream()
						.map(c -> c.getRecordFilenames())
						.flatMap(Arrays::stream)
						.collect(Collectors.toList());

			return new SimpleMDReferenceProvider(coordinateController, recordFilenames);
		}
		
		return new FileReferenceProvider(referencefile, coordinateController);
	}
	
	protected boolean filter(final Result result) {
		boolean isFiltered = false;
		// apply each filter
		for (final Filter filter : conditionContainer.getFilterContainer().getFilters()) {
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
			
			final ParallelData.Builder parallelDataBuilder = new ParallelData.Builder(
							conditionContainer.getConditionSize(), conditionContainer.getReplicateSizes());
			for (int conditionIndex = 0; conditionIndex < conditionContainer.getConditionSize() ; ++conditionIndex) {
				final ReplicateContainer replicateContainer = conditionContainer.getReplicatContainer(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicateContainer.getReplicateSize() ; ++replicateIndex) {
					DataTypeContainer replicate = conditionContainer.getDataContainer(conditionIndex, replicateIndex, coordinate);
					parallelDataBuilder.withReplicate(conditionIndex, replicateIndex, replicate);
				}	
			}
			parallelData = parallelDataBuilder.build();
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
	public ParallelData next() {
		if (! hasNext()) {
			return null;
		}
		coordinateController.advance();
		return parallelData;
	}

	public void doWork(final ParallelData parallelData) {
		final Result result = process(parallelData);
		if (result == null) {
			return;
		}

		if (getParameter().getFilterConfig().hasFiters()) {
			filter(result);
		}

		try {
			copyTmpResult.addResult(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract Result process(final ParallelData parallelData);

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
		conditionContainer.updateWindowCoordinates(coordinateController.next());
	}

	protected void processInit() {
		// try to get a new reservedWindoCoordinate
		Coordinate reserverdWindowCoordinate = null;
		synchronized (getWorkerDispatcherInstance()) {
			if (getWorkerDispatcherInstance().hasNext()) {
				getWorkerDispatcherInstance().getThreadIds().add(threadIdContainer.getThreadId());
				reserverdWindowCoordinate = getWorkerDispatcherInstance().next();
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
		ParallelData parallelData;
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

		synchronized (getWorkerDispatcherInstance()) {
			getWorkerDispatcherInstance().notify();
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
	
	private WorkerDispatcher getWorkerDispatcherInstance() {
		return method.getWorkerDispatcherInstance();
	}
	
	protected GeneralParameter getParameter() {
		return method.getParameter();
	}
	
	protected List<ConditionParameter> getConditionParameter() {
		return getParameter().getConditionParameters();
	}

	protected ConditionContainer getConditionContainer() {
		return conditionContainer;
	}
	
	public ThreadIdContainer getThreadIdContainer() {
		return threadIdContainer;
	}

	public CopyTmpResult getCopyTmpResult() {
		return copyTmpResult;
	}
	
	public int getComparisons() {
		return comparisons;
	}
	
}
