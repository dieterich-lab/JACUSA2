package lib.data.cache;

import java.util.Arrays;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.AbstractBaseCallRegionDataCache;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasRTarrestCount;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;

public class AlignmentDataCache<T extends AbstractData & HasReferenceBase & HasBaseCallCount & HasRTarrestCount & HasArrestBaseCallCount & HasThroughBaseCallCount> 
extends AbstractDataCache<T> {

	private final LIBRARY_TYPE libraryType;

	private final int[] readStartCount;
	private final int[] readEndCount;
	
	private final AbstractBaseCallRegionDataCache<T> startBC;
	private final AbstractBaseCallRegionDataCache<T> endBC;
	
	private final int[] coverage;
	
	public AlignmentDataCache(final LIBRARY_TYPE libraryType, final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {
		super(coordinateController);
		this.libraryType 	= libraryType;

		readStartCount 		= new int[coordinateController.getActiveWindowSize()];
		readEndCount 		= new int[coordinateController.getActiveWindowSize()];
		
		startBC 			= new AbstractBaseCallRegionDataCache<T>(maxDepth, minBASQ, baseCallConfig, coordinateController) {
			@Override
			public void addData(T data, Coordinate coordinate) {
				// nothing needed
			}
		};
		endBC 			= new AbstractBaseCallRegionDataCache<T>(maxDepth, minBASQ, baseCallConfig, coordinateController) {
			@Override
			public void addData(T data, Coordinate coordinate) {
				// nothing needed
			}
		};

		coverage 			= new int[coordinateController.getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		final AlignmentBlock first = record.getAlignmentBlocks().get(0);
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		
		int windowPosition1 = getCoordinateController().convert2windowPosition(first.getReferenceStart());
		if (windowPosition1 >= 0) {
			readStartCount[windowPosition1]++;
			startBC.addRecordWrapperRegion(first.getReferenceStart(), first.getReadStart() - 1, 1, recordWrapper);
		}
		
		int windowPosition2 = getCoordinateController().convert2windowPosition(last.getReferenceStart());
		if (windowPosition2 >= 0) {
			readEndCount[windowPosition2]++;
			final int length = last.getLength();
			endBC.addRecordWrapperRegion(last.getReferenceStart() + length - 1, last.getReadStart() + length - 2, 1, recordWrapper);
		}

		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			storeCoverage(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}
	
	protected void storeCoverage(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
	
		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			final int guardedWindowPosition = windowPositionGuard.getWindowPosition() + j;
			coverage[guardedWindowPosition]++;
		}
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (coverage[windowPosition] == 0) {
			return;
		}
		
		data.getRTarrestCount().setReadStart(readStartCount[windowPosition]);
		data.getRTarrestCount().setReadEnd(readEndCount[windowPosition]);
		final int inner	= coverage[windowPosition] - (data.getRTarrestCount().getReadStart() + data.getRTarrestCount().getReadEnd());
		data.getRTarrestCount().setReadInternal(inner);
		
		int arrest = 0;
		int through = 0;

		final BaseCallCount arrestBC = new BaseCallCount();
		final BaseCallCount throughBC = new BaseCallCount(data.getBaseCallCount());
		
		switch (libraryType) {

		case UNSTRANDED:
			arrest 	+= data.getRTarrestCount().getReadStart();
			arrest 	+= data.getRTarrestCount().getReadEnd();
			through	+= coverage[windowPosition] - (data.getRTarrestCount().getReadStart() + data.getRTarrestCount().getReadEnd());
			
			arrestBC.add(new BaseCallCount(startBC.getBaseCalls()[windowPosition]));
			arrestBC.add(new BaseCallCount(endBC.getBaseCalls()[windowPosition]));
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= data.getRTarrestCount().getReadEnd();
			through	+= coverage[windowPosition] - (data.getRTarrestCount().getReadEnd());

			arrestBC.add(new BaseCallCount(endBC.getBaseCalls()[windowPosition]));
			
			break;

		case FR_SECONDSTRAND:
			arrest 	+= data.getRTarrestCount().getReadStart();
			through	+= coverage[windowPosition] - (data.getRTarrestCount().getReadStart());
			
			arrestBC.add(new BaseCallCount(startBC.getBaseCalls()[windowPosition]));
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
		throughBC.substract(arrestBC);
		data.getArrestBaseCallCount().add(arrestBC);
		data.getThroughBaseCallCount().add(throughBC);
		
		data.setReferenceBase(getCoordinateController().getReferenceProvider().getReference(windowPosition));
		
		data.getRTarrestCount().setReadArrest(arrest);
		data.getRTarrestCount().setReadThrough(through);
	}
	
	@Override
	public void clear() {
		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
		Arrays.fill(coverage, 0);
		startBC.clear();
		endBC.clear();
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
