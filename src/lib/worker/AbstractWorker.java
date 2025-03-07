package lib.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import jacusa.filter.Filter;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.data.storage.container.ComplexSharedStorage;
import lib.data.storage.container.FileReferenceProvider;
import lib.data.storage.container.ReferenceProvider;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.container.SimpleMDReferenceProvider;
import lib.data.validator.paralleldata.CombinedParallelDataValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.estimate.MinkaParameter;
import lib.io.copytmp.CopyTmpResult;
import lib.stat.GenericStat;
import lib.stat.estimation.provider.DeletionEstCountProvider;
import lib.stat.estimation.provider.InsertionEstCountProvider;
import lib.util.coordinate.CoordinateController;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;
import lib.util.ConditionContainer;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;

/**
 * TODO add documentation
 */
public abstract class AbstractWorker
extends Thread
implements Iterator<ParallelData> {

	public enum STATUS {INIT, READY, FINISHED, BUSY, WAITING}

	private final AbstractMethod method;
	
	private final ThreadIdContainer threadIdContainer;
	private final CopyTmpResult copyTmpResult;
	
	private final ConditionContainer condContainer;
	private CoordinateController coordControl;

	private final ParallelDataValidator parallelDataValidator;
	
	private int comparisons;
	private STATUS status;
	
	private ParallelData parallelData;
	
	protected final List<GenericStat> genericStats;
	
	public AbstractWorker(final AbstractMethod method, final int threadId) {
		this.method = method;
		
		threadIdContainer = new ThreadIdContainer(threadId);

		copyTmpResult = getParameter().getResultFormat().createCopyTmp(threadId, method.getWorkerDispatcherInstance());
		
		condContainer 		= new ConditionContainer(getParameter());
		coordControl 	= new CoordinateController(getParameter().getActiveWindowSize(), condContainer);

		final ReferenceProvider referenceProvider 	= createReferenceProvider(coordControl);
		final SharedStorage sharedStorage 			= new ComplexSharedStorage(referenceProvider);
		condContainer.initReplicateContainer(sharedStorage, getParameter(), method);

		parallelDataValidator = new CombinedParallelDataValidator(method.createParallelDataValidators());

		comparisons = 0;
		status 		= STATUS.INIT;
		setName(AbstractTool.getLogger().getTool().getName() + " Worker " + threadId);
		
		genericStats = new ArrayList<GenericStat>(2);
		if (getParameter().showDeletionCount() ||
				getParameter().showInsertionCount() ||
				getParameter().showInsertionStartCount()) {
			final MinkaParameter minkaPrm = new MinkaParameter();
			if (getParameter().showDeletionCount()) {
				final DeletionEstCountProvider delCountProv = 
						new DeletionEstCountProvider(minkaPrm.getMaxIterations());
				genericStats.add(new GenericStat(minkaPrm, delCountProv, "deletion_score", "deletion_pvalue"));
			}
			if (getParameter().showInsertionCount() ||
					getParameter().showInsertionStartCount()) {
				final InsertionEstCountProvider insCountProv = 
						new InsertionEstCountProvider(minkaPrm.getMaxIterations());
				genericStats.add(new GenericStat(minkaPrm, insCountProv, "insertion_score", "insertion_pvalue"));
			}
		}
	}

	protected double[] processGenericStats(final Result result) {
		final double[] stats = new double[genericStats.size()];
		
		for (int i = 0; i < genericStats.size(); ++i) {
			final GenericStat genericStat = genericStats.get(i);
			final Result genericStatResult = genericStat.filter(result.getParellelData());
			result.getResultInfo().addAll(genericStatResult.getResultInfo());
			stats[i] = genericStatResult.getStat();
		}
		
		return stats;
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
		for (final Filter filter : condContainer.getFilterContainer().getFilters()) {
			if (filter.applyFilter(result)) {
				isFiltered = true;
			}
		}

		result.setFiltered(isFiltered);
		return isFiltered;
	}
	
	@Override
	public boolean hasNext() {
		while (true) {
			while (coordControl.checkCoordAdvancerWithinActiveWindow()) {
				final Coordinate coord = coordControl.getCoordAdvancer()
						.getCurrentCoordinate().copy();
				
				final ParallelData.Builder parallelDataBuilder = new ParallelData.Builder(
								condContainer.getConditionSize(), condContainer.getReplicateSizes());
				parallelData = createParallelData(parallelDataBuilder, coord);
				if (parallelData != null && parallelDataValidator.isValid(parallelData)) {
					comparisons++;
					return true;
				}
				coordControl.advance();
			}
			
			if (coordControl.hasNext()) {
				final Coordinate activeWinCoord = coordControl.next();
				condContainer.updateActiveWinCoord(activeWinCoord);
			} else {
				return false;
			}
		}
	}
	
	final protected ParallelData createParallelData(lib.data.ParallelData.Builder parallelDataBuilder, Coordinate coord) {
		for (int condI = 0; condI < getConditionContainer().getConditionSize() ; ++condI) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(condI);
			for (int replicateI = 0; replicateI < replicateContainer.getReplicateSize() ; ++replicateI) {
				final DataContainer replicate = getConditionContainer().getNullDataContainer(condI, replicateI, coord);
				if (replicate == null) {
					return null;
				}
				parallelDataBuilder.withReplicate(condI, replicateI, replicate);
			}	
		}
		return parallelDataBuilder.build();
	}
	
	@Override
	public ParallelData next() {
		coordControl.advance();
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
			System.exit(1);
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
		
		coordControl.updateReserved(reservedWindowCoordinate);
		condContainer.updateWindowCoordinates(coordControl.next());
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
		
		while (hasNext()) {
			doWork(next());	
		}
		status = STATUS.INIT;
	}

	@Override
	public final void run() {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public STATUS getStatus() {
		return status;
	}

	protected void setStatus(final STATUS status) {
		this.status = status;
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
		return condContainer;
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
