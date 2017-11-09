package lib.tmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.builder.SAMRecordWrapperIterator;
import lib.io.variant.AbstractVariantFormat;
import lib.util.Coordinate;
import lib.variant.Variant;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;
import lib.worker.ThreadIdContainer;

/**
 * 
 * @author Michael Piechotta
 *
 * 
 */
public class AbstractOverlapWorker<T extends AbstractData> 
extends AbstractWorker<T> {

	/*
	private final TmpWorkerWriter<T> tmpWriter;
	private TmpWorkerReader<T> tmpReader;
	*/

	private final SAMRecordModifier recordModifier;
	
	private CoordinateController coordinateController;
	private final List<OverlappingRecordWrapperContainer> windowContainers;
	
	public AbstractOverlapWorker(final WorkerDispatcher<T> workerDispatcher, 
			final int threadId) throws IOException {
		super(workerDispatcher, threadId, parameters);

		windowContainers = createOverlappingContainers(getConditionParamterers().size());
	}

	private List<OverlappingRecordWrapperContainer> createOverlappingContainers(final int conditions) {
		final List<OverlappingRecordWrapperContainer> container = new ArrayList<OverlappingRecordWrapperContainer>(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			container.add(new OverlappingRecordWrapperContainer());
		}
		return container;
	}

	private int processLeft(final int[] recordCount) {
		int variantCount = 0;
		
		return variantCount;
	}
	
	private void processRecordModifier() {
		AddVariants.getLogger().addInfo("Thread " + (threadIdContainer.getThreadId() + 1) + ": " +
				"Implanting variants to contig " + 
				coordinateController.getReserved().getContig() + ":" + 
				coordinateController.getReserved().getStart() + "-" + 
				coordinateController.getReserved().getEnd());
		
		// counter to reconstruct order from tmp writers
		final int[] recordCount = new int[getConditionParamterers().size()];
		int variantCount = 0;
					
		while (coordinateController.hasNext()) {
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
		
		tmpWriter.updateCounts(variantCount, recordCount);
	}

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
	
	private void clear() {
		/*
		if (coordinateController.isLeft()) {

		}
		*/

		if (coordinateController.isInner()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterers().size(); conditionIndex++) {
				windowContainers.get(conditionIndex).getLeft().clear();
			}		
		}
		
		if (coordinateController.isRight()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterers().size(); conditionIndex++) {
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
	
	// TODO keep track of closeable iterator
	private List<Iterator<SAMRecordWrapper>> createIterators(final Coordinate activeWindowCoordinate, final Coordinate reserverdWindowCoordinate) {
		final List<Iterator<SAMRecordWrapper>> iterators = 
				new ArrayList<Iterator<SAMRecordWrapper>>(getConditionParamterers().size());
		if (coordinateController.isLeft()) {
			// TODO left outer already computed
			for (int conditionIndex = 0; conditionIndex < getConditionParamterers().size(); conditionIndex++) {
				final SAMRecordWrapperIterator iterator = recordProviders.get(conditionIndex).getIterator(activeWindowCoordinate, reserverdWindowCoordinate);
				iterators.add(iterator);
			}
		}

		if (coordinateController.isInner()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterers().size(); conditionIndex++) {
				final List<Iterator<SAMRecordWrapper>> tmpIterators = new ArrayList<Iterator<SAMRecordWrapper>>(2);
				// get it to reads that overlap active window on left site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());
				// get it to reads that are within this active window ( ] - overlapping right side of window
				tmpIterators.add(recordProviders.get(conditionIndex).getIterator(activeWindowCoordinate, reserverdWindowCoordinate));

				final Iterator<SAMRecordWrapper> iterator = new CombinedSAMRecordWrapperIterator(tmpIterators);
				iterators.add(iterator);
			}			
		}
		
		if (coordinateController.isRight()) {
			for (int conditionIndex = 0; conditionIndex < getConditionParamterers().size(); conditionIndex++) {
				final List<Iterator<SAMRecordWrapper>> tmpIterators = new ArrayList<Iterator<SAMRecordWrapper>>(2);
				// get it to reads that overlap active window on left site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());
				// get it to reads that are within this active window ( ] - overlapping right side of window
				tmpIterators.add(recordProviders.get(conditionIndex).getIterator(activeWindowCoordinate, reserverdWindowCoordinate));
				// get it to reads that overlap active window on right site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());

				final Iterator<SAMRecordWrapper> iterator = new CombinedSAMRecordWrapperIterator(tmpIterators);
				iterators.add(iterator);
			}			
		}

		return iterators;
	}

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
