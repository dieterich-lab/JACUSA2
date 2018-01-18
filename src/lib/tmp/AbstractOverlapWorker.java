package lib.tmp;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public abstract class AbstractOverlapWorker<T extends AbstractData, R extends Result<T>> 
extends AbstractWorker<T, R> {

	private final List<OverlappingRecordWrapperContainer> windowContainers;
	
	public AbstractOverlapWorker(final WorkerDispatcher<T, R> workerDispatcher,
			final int threadId, 
			final CopyTmpResult<T, R> copyTmpResult, 
			final List<ParallelDataValidator<T>> parallelDataValidators, 
			final AbstractParameter<T, R> generalParameter) throws IOException {
		super(workerDispatcher, threadId, copyTmpResult, parallelDataValidators, generalParameter);

		windowContainers = createOverlappingContainers(generalParameter.getConditionsSize());
	}
	
	private List<OverlappingRecordWrapperContainer> createOverlappingContainers(final int conditions) {
		final List<OverlappingRecordWrapperContainer> container = new ArrayList<OverlappingRecordWrapperContainer>(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			container.add(new OverlappingRecordWrapperContainer());
		}
		return container;
	}

	/* TODO
	private int processLeft(final int[] recordCount) {
		int variantCount = 0;
		
		return variantCount;
	}
	*/
	
	/* TODO
	private void processRecordModifier() {
		/*
		// counter to reconstruct order from tmp writers
		final int[] recordCount = new int[getConditionParamterers().size()];
		int variantCount = 0;
					
			// get next active window within reserved
			final Coordinate active = coordinateController.next();
			// get iterator for SAMRecords within active window 
			final List<Iterator<SAMRecordWrapper>> iterators = createIterators(active, coordinateController.getReserved());
			final List<List<SAMRecordWrapper>> readRecords = recordModifier.build(active, iterators);
			
			// mutate and write variants - SAMRecords in readRecords might be changed
			variantCount += createAndWriteVariants();

			// write records
			writeRecords(readRecords, recordCount);

			// clear overlapping container(s)
			clear();
		}		
		
		// TODO tmpWriter.updateCounts(variantCount, recordCount);

	}
	*/

	/* TODO
	private int createAndWriteVariants() {
		int variantCount = 0;
		while (recordModifier.hasNext()) {
			Variant[] variants = recordModifier.next();
			try {
				tmpWriter.getVariantWriter().addVariants(variants, getConditionParamterers());
			} catch (Exception e) {
				e.printStackTrace();
			}
			variantCount++;
		}
		return variantCount;
	}
	*/

	/* TODO
	private void writeRecords(final List<List<SAMRecordWrapper>> readRecords, final int[] recordCount) {
		for (int conditionIndex = 0; conditionIndex < getConditionParamterers().size(); conditionIndex++) {
			final SAMFileWriter tmpRecordWriter = tmpWriter.getRecordWriter(conditionIndex);
			for (final SAMRecordWrapper recordWrapper : readRecords.get(conditionIndex)) {
				if (! recordWrapper.overlapsWindowBorders()) {
					tmpRecordWriter.addAlignment(recordWrapper.getSAMRecord());
					recordWrapper.setPrinted();
					recordCount[conditionIndex]++;
				}
			}
		}
	}
	*/
	
	/* TODO
	private void clear() {
		
		if (coordinateController.isLeft()) {

		}
		*/

		/*
		if (getCoordinateController().isInner()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterer().size(); conditionIndex++) {
				windowContainers.get(conditionIndex).getLeft().clear();
			}		
		}
		
		if (getCoordinateController().isRight()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterer().size(); conditionIndex++) {
				windowContainers.get(conditionIndex).clear();
			}
		}
	}
	
	
	private boolean hasLeft() {
		for (final OverlappingRecordWrapperContainer container : windowContainers) {
			if (! container.getLeft().isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasRight() {
		for (final OverlappingRecordWrapperContainer container : windowContainers) {
			if (! container.getRight().isEmpty()) {
				return true;
			}
		}

		return false;
	}
	*/
	
	/*
	// TODO keep track of closeable iterator
	private List<Iterator<SAMRecordWrapper>> createIterators(final Coordinate activeWindowCoordinate, final Coordinate reserverdWindowCoordinate) {
		final List<Iterator<SAMRecordWrapper>> iterators = 
				new ArrayList<Iterator<SAMRecordWrapper>>(getConditionParamterer().size());
		if (getCoordinateController().isLeft()) {
			// TODO left outer already computed
			for (int conditionIndex = 0; conditionIndex < getConditionParamterer().size(); conditionIndex++) {
				final SAMRecordWrapperIterator iterator = recordProviders.get(conditionIndex).createIterator(activeWindowCoordinate, reserverdWindowCoordinate);
				iterators.add(iterator);
			}
		}

		if (getCoordinateController().isInner()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterer().size(); conditionIndex++) {
				final List<Iterator<SAMRecordWrapper>> tmpIterators = new ArrayList<Iterator<SAMRecordWrapper>>(2);
				// get it to reads that overlap active window on left site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());
				// get it to reads that are within this active window ( ] - overlapping right side of window
				tmpIterators.add(recordProviders.get(conditionIndex).createIterator(activeWindowCoordinate, reserverdWindowCoordinate));

				final Iterator<SAMRecordWrapper> iterator = new CombinedSAMRecordWrapperIterator(tmpIterators);
				iterators.add(iterator);
			}			
		}
		
		if (coordinateController.isRight()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterer().size(); conditionIndex++) {
				final List<Iterator<SAMRecordWrapper>> tmpIterators = new ArrayList<Iterator<SAMRecordWrapper>>(2);
				// get it to reads that overlap active window on left site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());
				// get it to reads that are within this active window ( ] - overlapping right side of window
				tmpIterators.add(recordProviders.get(conditionIndex).createIterator(activeWindowCoordinate, reserverdWindowCoordinate));
				// get it to reads that overlap active window on right site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());

				final Iterator<SAMRecordWrapper> iterator = new CombinedSAMRecordWrapperIterator(tmpIterators);
				iterators.add(iterator);
			}			
		}

		return iterators;
	}
	*/

	/* TODO
	public TmpWorkerReader<T> getTmpReader() {
		return tmpReader;
	}
	
	public TmpWorkerWriter<T> getTmpWriter() {
		return tmpWriter;
	}
	*/

	public List<OverlappingRecordWrapperContainer> getWindowContainer() {
		return windowContainers;
	}

	/*
	if (isInnerWindow()) {
		
	} else if (isLeftWindow()) {
		
	} else if (isRightWindow()) {
		
	} else {
		throw new IllegalStateException(); // TODO add text
	}
	*/

}
