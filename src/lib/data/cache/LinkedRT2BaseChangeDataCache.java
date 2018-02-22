package lib.data.cache;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.LinkedReadArrestCount;
import lib.data.ReadArrestCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasCoverage;
import lib.data.has.hasLinkedReadArrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

public class LinkedRT2BaseChangeDataCache<T extends AbstractData & hasCoverage & hasBaseCallCount & hasReferenceBase & hasLinkedReadArrestCount> 
extends AbstractDataCache<T> {

	private final LIBRARY_TYPE libraryType;

	private final List<List<SimpleEntry<Integer, Integer>>> readStart;
	private final List<List<SimpleEntry<Integer, Integer>>> readEnd;
	
	private final Set<Integer> windowPositions;
	
	private final int[] readStartCount;
	private final int[] readEndCount;
	
	private BaseCallDataCache<T> baseCallDataCache;
	
	public LinkedRT2BaseChangeDataCache(final LIBRARY_TYPE libraryType,
			final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		baseCallDataCache = new BaseCallDataCache<T>(maxDepth, minBASQ, baseCallConfig, coordinateController);
		
		this.libraryType = libraryType;
	
		readStartCount = new int[coordinateController.getActiveWindowSize()];
		readEndCount = new int[coordinateController.getActiveWindowSize()];
		
		readStart = new ArrayList<List<SimpleEntry<Integer,Integer>>>(coordinateController.getActiveWindowSize());
		readEnd = new ArrayList<List<SimpleEntry<Integer,Integer>>>(coordinateController.getActiveWindowSize());
		
		for (int i = 0; i < coordinateController.getActiveWindowSize(); ++i) {
			readStart.add(new ArrayList<SimpleEntry<Integer,Integer>>(10));
			readEnd.add(new ArrayList<SimpleEntry<Integer,Integer>>(10));
		}
		
		windowPositions = new HashSet<Integer>(coordinateController.getActiveWindowSize());
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();

		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		if (windowPosition1 >= 0) {
			readStartCount[windowPosition1]++;
		}
		
		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());
		if (windowPosition2 >= 0) {
			readEndCount[windowPosition2]++;
		}
		
		if (windowPosition1 < 0 && windowPosition2 < 0) {
			return;
		}

		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			parseTODO(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}
	
	protected void parseTODO(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
	
		final SAMRecord record = recordWrapper.getSAMRecord();
		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			final int guardedReferencePosition = windowPositionGuard.getReferencePosition() + j;
			final int guardedWindowPosition = windowPositionGuard.getWindowPosition() + j;
			final int guardedReadPosition = windowPositionGuard.getReadPosition() + j;
			
			final byte bc = record.getReadBases()[guardedReadPosition];
			final int baseIndex = baseCallDataCache.getBaseCallConfig().getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}
			final byte bq = record.getBaseQualities()[guardedReadPosition];
			if (bq < baseCallDataCache.getMinBaseCallQuality()) {
				continue;
			}

			byte refBase = getCoordinateController().getReferenceProvider().getReference(guardedWindowPosition);
			// ignore N reference
			if (baseCallDataCache.getBaseCallConfig().getBaseIndex(refBase) < 0) {
				continue;
			}

			baseCallDataCache.incrementBaseCall(guardedWindowPosition, guardedReadPosition, baseIndex, bq);
			
			if (refBase != bc) {
				// mismatch
				if (windowPosition1 >= 0) {
					add(windowPosition1, guardedReferencePosition, baseIndex, readStart);
				}
				
				if (windowPosition2 >= 0) {
					add(windowPosition2, guardedReferencePosition, baseIndex, readEnd);
				}
			}
		}
	}
	
	private void add(final int windowPosition, final int reference, final int baseIndex, final List<List<SimpleEntry<Integer, Integer>>> win2refBc) {
		windowPositions.add(windowPosition);
		win2refBc.get(windowPosition).add(new SimpleEntry<Integer, Integer>(reference, baseIndex));
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		// add base calls
		baseCallDataCache.addData(data, coordinate);

		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);

		final LinkedReadArrestCount linkedReadArrestCount = data.getLinkedReadArrestCount();
		final ReadArrestCount readArrestCount = linkedReadArrestCount.getReadArrestCount();
		
		readArrestCount.setReadStart(readStartCount[windowPosition]);
		readArrestCount.setReadEnd(readEndCount[windowPosition]);

		final int internal = baseCallDataCache.getCoverage()[windowPosition] - 
				(readArrestCount.getReadStart() + readArrestCount.getReadEnd());
		readArrestCount.setReadInternal(internal);

		int arrest = 0;
		int through = 0;

		switch (libraryType) {

		case UNSTRANDED:
			arrest 	+= readArrestCount.getReadStart();
			arrest 	+= readArrestCount.getReadEnd();
			through += readArrestCount.getReadInternal();
			
			add2ReadInfoExtendedCount(windowPosition, readStart, linkedReadArrestCount, true);
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= readArrestCount.getReadEnd();
			through += readArrestCount.getReadInternal();
			
			add2ReadInfoExtendedCount(windowPosition, readEnd, linkedReadArrestCount, true);
			break;

		case FR_SECONDSTRAND:
			arrest 	+= readArrestCount.getReadStart();
			through += readArrestCount.getReadInternal();
			
			add2ReadInfoExtendedCount(windowPosition, readStart, linkedReadArrestCount, true);
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		data.setReferenceBase(getCoordinateController().getReferenceProvider().getReference(windowPosition));
		
		readArrestCount.setReadArrest(arrest);
		readArrestCount.setReadThrough(through);
	}
	
	private void add2ReadInfoExtendedCount(final int windowPosition, 
			final List<List<SimpleEntry<Integer, Integer>>> win2refBc,  
			final LinkedReadArrestCount readInfoExtendedCount, final boolean arrest) {

		if (windowPosition >= 0 && windowPosition < win2refBc.size()) {
			for (int i = 0; i < win2refBc.get(windowPosition).size(); ++i) {
				final int referencePosition = win2refBc.get(windowPosition).get(i).getKey();
				final int baseIndex = win2refBc.get(windowPosition).get(i).getValue();
				if (arrest) {
					readInfoExtendedCount.add2arrest(referencePosition, baseIndex);
				} else {
					readInfoExtendedCount.add2through(referencePosition, baseIndex);
				}
			}
		}
	}
	
	@Override
	public void clear() {
		baseCallDataCache.clear();
		
		for (int windowPosition : windowPositions) {
			readStart.get(windowPosition).clear();
			readEnd.get(windowPosition).clear();
		}
		windowPositions.clear();
		
		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
	}

	public int getReadStartCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readStartCount[windowPosition];
	}

	public int getReadEndCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readEndCount[windowPosition];
	}

	public BaseCallDataCache<T> getBaseCallDataCache() {
		return baseCallDataCache; 
	}

}
