package lib.data.cache;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.LRTarrestCount;
import lib.data.RTarrestCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

public abstract class CopyOfLRTarrest2BaseCallDataCache<T extends AbstractData & hasBaseCallCount & hasReferenceBase> 
extends AbstractDataCache<T> {

	private final LIBRARY_TYPE libraryType;
	
	private final BaseCallConfig baseCallConfig;
	
	// readStart of read -> reference position of mismatch position -> baseCallCount of mismatch position
	private final int[][][] readStart2ref2baseCalls;
	private final int[][][] readEnd2ref2baseCalls;
	
	// readStart[i] -> positions in readStart2ref2baseCalls[i][]
	private final List<List<Integer>> readStart2windowPositions;
	private final List<List<Integer>> readEnd2windowPositions;

	private final int[] readStartCount;
	private final int[] readEndCount;
	private final int[] coverageWithBQ;
	private final int[] coverageWithoutBQ;

	private final int[][] baseCalls;
	private final boolean[] nonRefBaseCalls;
	
	private final Set<Integer> windowPositions;
	
	public CopyOfLRTarrest2BaseCallDataCache(final LIBRARY_TYPE libraryType, final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.libraryType = libraryType;

		this.baseCallConfig = baseCallConfig;
		
		readStartCount 		= new int[coordinateController.getActiveWindowSize()];
		readEndCount 		= new int[coordinateController.getActiveWindowSize()];
		coverageWithBQ		= new int[coordinateController.getActiveWindowSize()];
		coverageWithoutBQ	= new int[coordinateController.getActiveWindowSize()];
		
		final int n = coordinateController.getActiveWindowSize();
		readStart2ref2baseCalls = new int[n][n][baseCallConfig.getBases().length];
		readEnd2ref2baseCalls 	= new int[n][n][baseCallConfig.getBases().length];

		readStart2windowPositions 	= new ArrayList<List<Integer>>(n);
		readEnd2windowPositions 	= new ArrayList<List<Integer>>(n);
		windowPositions				= new HashSet<Integer>(n);
		
		for (int i = 0; i < n; ++i) {
			readStart2windowPositions.add(new ArrayList<Integer>(n));
			readEnd2windowPositions.add(new ArrayList<Integer>(n));
		}
		
		baseCalls 		= new int[n][baseCallConfig.getBases().length];
		nonRefBaseCalls = new boolean[n];
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

			coverageWithBQ[guardedWindowPosition]++;

			// check baseCall is not "N"
			final byte bc = record.getReadBases()[guardedReadPosition];
			final int baseIndex = baseCallConfig.getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}

			// check baseCall quality
			final byte bq = record.getBaseQualities()[windowPositionGuard.getReadPosition() + j];
			// TODO
			
			byte refBase = getCoordinateController().getReferenceProvider().getReference(guardedWindowPosition);
			// TODO
			// ignore N reference
			if (baseCallConfig.getBaseIndex(refBase) < 0) {
				continue;
			}
			
			if (refBase != bc) {
				// mismatch
				if (windowPosition1 >= 0) {
					add(windowPosition1, guardedReadPosition, guardedReferencePosition, baseIndex, readStart2ref2baseCalls);
				}
				
				if (windowPosition2 >= 0) {
					add(windowPosition2, guardedReadPosition, guardedReferencePosition, baseIndex, readEnd2ref2baseCalls);
				}
			}
		}
	}
	
	protected void add(final int windowPosition, final int readPosition, final int reference, 
			final int baseIndex, final List<List<SimpleEntry<Integer, Integer>>> win2refBc) {

		windowPositions.add(windowPosition);
		win2refBc.get(windowPosition).add(new SimpleEntry<Integer, Integer>(reference, baseIndex));
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);

		final LRTarrestCount linkedReadArrestCount = data.getLRTarrestCount();
		final RTarrestCount readArrestCount = linkedReadArrestCount.getRTarrestCount();
		
		readArrestCount.setReadStart(readStartCount[windowPosition]);
		readArrestCount.setReadEnd(readEndCount[windowPosition]);

		// TODO TEST if coverage is calculated correctly
		final int internal = coverageWithBQ[windowPosition] - 
				(readArrestCount.getReadStart() + readArrestCount.getReadEnd());
		readArrestCount.setReadInternal(internal);

		int arrest = 0;
		int through = 0;

		switch (libraryType) {

		case UNSTRANDED:
			arrest 	+= readArrestCount.getReadStart();
			arrest 	+= readArrestCount.getReadEnd();
			through += readArrestCount.getReadInternal();
			
			add2ReadInfoExtendedCount(windowPosition, readStart2ref2baseCalls, linkedReadArrestCount, true);
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= readArrestCount.getReadEnd();
			through += readArrestCount.getReadInternal();
			
			add2ReadInfoExtendedCount(windowPosition, readEnd2ref2baseCalls, linkedReadArrestCount, true);
			break;

		case FR_SECONDSTRAND:
			arrest 	+= readArrestCount.getReadStart();
			through += readArrestCount.getReadInternal();
			
			add2ReadInfoExtendedCount(windowPosition, readStart2ref2baseCalls, linkedReadArrestCount, true);
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
			final LRTarrestCount readInfoExtendedCount, final boolean arrest) {

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
		for (int windowPosition : windowPositions) {
			readStart2ref2baseCalls.get(windowPosition).clear();
			readEnd2ref2baseCalls.get(windowPosition).clear();
		}
		windowPositions.clear();

		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
		Arrays.fill(coverageWithBQ, 0);
	}

	public int getReadStartCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readStartCount[windowPosition];
	}

	public int getReadEndCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readEndCount[windowPosition];
	}

}
