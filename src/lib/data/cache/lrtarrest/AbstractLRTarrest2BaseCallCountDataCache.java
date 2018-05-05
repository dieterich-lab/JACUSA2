package lib.data.cache.lrtarrest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.LRTarrestCount;
import lib.data.RTarrestCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.AbstractDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public abstract class AbstractLRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractDataCache<T> {

	private final LIBRARY_TYPE libraryType;
	private final BaseCallConfig baseCallConfig;
	private final byte minBASQ;

	private final LRTarrest2BaseCallCount start;
	private final LRTarrest2BaseCallCount end;
	
	private Map<Integer, Byte> ref2base;
	
	private final int[] coverageWithoutBQ;
	private final int[][] baseCalls;
	
	public AbstractLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.libraryType = libraryType;
		this.minBASQ = minBASQ;
		this.baseCallConfig = baseCallConfig;
		
		final int activeWindowSize = coordinateController.getActiveWindowSize();
		start 				= new LRTarrest2BaseCallCount(coordinateController);
		end 				= new LRTarrest2BaseCallCount(coordinateController);
		coverageWithoutBQ	= new int[activeWindowSize];
		ref2base			= new HashMap<Integer, Byte>(100);
		baseCalls 			= new int[activeWindowSize][baseCallConfig.getBases().length];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();

		int alignmentStartWindowPosition = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		if (alignmentStartWindowPosition >= 0) {
			start.addArrest(alignmentStartWindowPosition);
		}

		int alignmentEndWindowPosition = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());
		if (alignmentEndWindowPosition >= 0) {
			end.addArrest(alignmentEndWindowPosition);
		}

		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final WindowPositionGuard windowPositionGuard = 
					getCoordinateController().convert(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength());

			addRecordWrapperRegion(windowPositionGuard.getReferencePosition(), 
					windowPositionGuard.getReadPosition(), 
					windowPositionGuard.getLength(), recordWrapper);
		}
	}

	public void addRecordWrapperRegion(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final int windowPosition = getCoordinateController().convert2windowPosition(referencePosition);
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		int alignmentStartWindowPosition = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int alignmentEndWindowPosition = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < length; ++j) {
			final int tmpReferencePosition 	= referencePosition + j;
			final int tmpWindowPosition 	= windowPosition < 0 ? -1 : windowPosition + j;
			final int tmpReadPosition 		= readPosition + j;

			// check baseCall is not "N"
			final byte bc = record.getReadBases()[tmpReadPosition];
			final int baseIndex = baseCallConfig.getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}

			if (tmpWindowPosition >= 0) {
				// ignore base quality here 
				coverageWithoutBQ[tmpWindowPosition]++;
			}

			// check baseCall quality
			final byte bq = record.getBaseQualities()[tmpReadPosition];
			if (bq < minBASQ) {
				continue;
			}
			// count base call
			baseCalls[tmpWindowPosition][baseIndex]++;
			
			add(alignmentStartWindowPosition, tmpReferencePosition, tmpReadPosition, baseIndex, recordWrapper, start);
			add(alignmentEndWindowPosition, tmpReferencePosition, tmpReadPosition, baseIndex, recordWrapper, end);
		}
	}
	
	private void add(final int windowArrestPosition, final int baseCallReferencePosition, final int readPosition, final int baseIndex, 
			final SAMRecordWrapper recordWrapper, LRTarrest2BaseCallCount dest) {

		if (windowArrestPosition < 0) {
			return; 
		}

		// check if we are outside of window
		final int baseCallWindowPosition = getCoordinateController().convert2windowPosition(baseCallReferencePosition);
		if (baseCallWindowPosition < 0) {
			if (! ref2base.containsKey(baseCallReferencePosition)) {
				ref2base.put(baseCallReferencePosition, recordWrapper.getReference()[readPosition]);
			}
		}
		dest.addBaseCall(windowArrestPosition, baseCallReferencePosition, baseIndex);
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int winArrestPos = getCoordinateController().convert2windowPosition(coordinate);

		final LRTarrestCount lrtArrestCount = data.getLRTarrestCount();
		final RTarrestCount readArrestCount = lrtArrestCount.getRTarrestCount();
		
		readArrestCount.setReadStart(start.getArrestCount(winArrestPos));
		readArrestCount.setReadEnd(end.getArrestCount(winArrestPos));
		final int inner = coverageWithoutBQ[winArrestPos] - 
				(readArrestCount.getReadStart() + readArrestCount.getReadEnd());
		readArrestCount.setReadInternal(inner);
		
		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE) {
			invert = true;
		}

		int arrest = 0;
		int through = 0;
		
		switch (libraryType) {

		
		case UNSTRANDED:
			arrest 	+= readArrestCount.getReadStart();
			arrest 	+= readArrestCount.getReadEnd();
			through += coverageWithoutBQ[winArrestPos] - 
					(readArrestCount.getReadStart() + readArrestCount.getReadEnd());

			add(start, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest(), lrtArrestCount.getReference());
			add(end, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest(), lrtArrestCount.getReference());
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= readArrestCount.getReadEnd();
			through += coverageWithoutBQ[winArrestPos] - 
					(readArrestCount.getReadEnd());

			add(end, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest(), lrtArrestCount.getReference());
			break;

		case FR_SECONDSTRAND:
			arrest 	+= readArrestCount.getReadStart();
			through += coverageWithoutBQ[winArrestPos] - 
					(readArrestCount.getReadStart());

			add(start, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest(), lrtArrestCount.getReference());
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		// FIXME this should be done somewhere else
		data.setReferenceBase(getCoordinateController().getReferenceProvider().getReference(winArrestPos));

		// add base call data
		System.arraycopy(baseCalls[winArrestPos], 0, 
				data.getBaseCallCount().getBaseCallCount(), 0, baseCallConfig.getBases().length);
		if (invert) {
			data.getBaseCallCount().invert();
		}

		readArrestCount.setReadArrest(arrest);
		readArrestCount.setReadThrough(through);
	}

	private void add(final LRTarrest2BaseCallCount lrtArrestbcc, final int winArrestPos, boolean invert, 
			final Map<Integer, BaseCallCount> arrestRef2bc, final Map<Integer, Byte> reference) {
		if (lrtArrestbcc.getRef2bc(winArrestPos) == null) {
			return;
		}

		for (final int refPosition : lrtArrestbcc.getRef2bc(winArrestPos).keySet()) {
			byte refBase = 'N';

			// check if we are outside of window
			final int baseCallWindowPosition = getCoordinateController().convert2windowPosition(refPosition);
			if (baseCallWindowPosition < 0) {
				refBase = ref2base.get(refPosition);
			} else {
				// get reference base from common storage within window
				refBase = getCoordinateController().getReferenceProvider().getReference(baseCallWindowPosition);
			}
			if (refBase == 'N') {
				continue;
			}

			if (! arrestRef2bc.containsKey(refPosition)) {
				arrestRef2bc.put(refPosition, new BaseCallCount());
			}

			reference.put(refPosition, refBase);

			final BaseCallCount baseCallCount = lrtArrestbcc.getRef2bc(winArrestPos).get(refPosition);
			baseCallCount.set(BaseCallConfig.getInstance().getBaseIndex(refBase), 0);
			if (invert) {
				baseCallCount.invert();
			}
			arrestRef2bc.get(refPosition).add(baseCallCount);
		}
	}
	
	@Override
	public void clear() {
		start.clear();
		end.clear();

		Arrays.fill(coverageWithoutBQ, 0);
		if (ref2base.size() < 100) {
			ref2base.clear();
		} else {
			ref2base = new HashMap<Integer, Byte>(100);
		}
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
}
