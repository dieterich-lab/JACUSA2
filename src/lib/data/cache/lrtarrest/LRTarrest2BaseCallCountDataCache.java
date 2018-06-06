package lib.data.cache.lrtarrest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.SequenceUtil;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.lrtarrest.RefPos2BaseCallCountExtractor;
import lib.data.cache.extractor.lrtarrest.LRTarrestCountExtractor;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.BaseCallCount;
import lib.data.count.LRTarrestCount;
import lib.data.count.RTarrestCount;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public class LRTarrest2BaseCallCountDataCache<T extends AbstractData> 
extends AbstractDataAdder<T>
implements RegionDataCache<T>, RecordWrapperDataCache<T> {

	private final RefPos2BaseCallCountExtractor<T> refPos2BaseCallCountExtractor;
	private final LRTarrestCountExtractor<T> lrtArrestCountExtractor;
	
	private final LIBRARY_TYPE libraryType;
	private final byte minBASQ;

	private final ArrestPos2RefPos2BaseCallCount start;
	private final ArrestPos2RefPos2BaseCallCount end;

	private Map<Integer, Byte> ref2base;
	
	private final int[] coverageWithoutBQ;
	private final ArrayBaseCallAdder<T> baseCallRegionDataCache;
	
	public LRTarrest2BaseCallCountDataCache(
			final BaseCallCountExtractor<T> baseCallCountExtractor, 
			final RefPos2BaseCallCountExtractor<T> refPos2BaseCallCountExtractor,
			final LRTarrestCountExtractor<T> lrtArrestCountExtractor,
			final LIBRARY_TYPE libraryType, final int maxDepth, final byte minBASQ,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.refPos2BaseCallCountExtractor 	= refPos2BaseCallCountExtractor;
		this.lrtArrestCountExtractor		= lrtArrestCountExtractor;
		
		this.libraryType 	= libraryType;
		this.minBASQ 		= minBASQ;
		
		final int activeWindowSize = coordinateController.getActiveWindowSize();
		start 				= new ArrestPos2RefPos2BaseCallCount(activeWindowSize);
		end 				= new ArrestPos2RefPos2BaseCallCount(activeWindowSize);

		ref2base			= new HashMap<Integer, Byte>(100);
		
		coverageWithoutBQ	= new int[activeWindowSize];
		// FIXME 
		baseCallRegionDataCache = null;
		/* FIXME
		baseCallRegionDataCache = new ArrayBaseCallAdder<T>(
				baseCallCountExtractor, 
				maxDepth, minBASQ, 
				coordinateController);
				*/
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

			addRegion(windowPositionGuard.getReferencePosition(), 
					windowPositionGuard.getReadPosition(), 
					windowPositionGuard.getLength(), recordWrapper);
		}
	}

	@Override
	public void addRegion(int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper) {
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final int windowPosition = getCoordinateController().convert2windowPosition(referencePosition);
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		int alignmentStartWindowPosition = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int alignmentEndWindowPosition = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < length; ++j) {
			final int refPosBC 				= referencePosition + j;
			final int tmpWindowPosition 	= windowPosition < 0 ? -1 : windowPosition + j;
			final int tmpReadPosition 		= readPosition + j;

			// check baseCall is not "N"
			final byte bc = record.getReadBases()[tmpReadPosition];
			if (! SequenceUtil.isValidBase(bc)) {
				continue;
			}
			final Base base = Base.valueOf(bc);

			if (tmpWindowPosition >= 0) {
				// ignore base quality here 
				coverageWithoutBQ[tmpWindowPosition]++;
			}

			// check baseCall quality
			final byte baseQuality = record.getBaseQualities()[tmpReadPosition];
			if (baseQuality < minBASQ) {
				continue;
			}
			
			// count base call
			baseCallRegionDataCache.increment(refPosBC, tmpWindowPosition, tmpReadPosition, base, baseQuality, record);
			
			add(alignmentStartWindowPosition, refPosBC, tmpReadPosition, base, recordWrapper, start);
			add(alignmentEndWindowPosition, refPosBC, tmpReadPosition, base, recordWrapper, end);
		}
	}
	
	private void add(final int winArrestPos, final int refPosBC, final int readPosition, final Base base, 
			final SAMRecordWrapper recordWrapper, ArrestPos2RefPos2BaseCallCount dest) {

		if (winArrestPos < 0) {
			return; 
		}
	
		final int winPosBC = getCoordinateController().convert2windowPosition(refPosBC);
		if (winPosBC < 0 && ! ref2base.containsKey(refPosBC)) {
			final byte refBase = recordWrapper.getReference()[readPosition];
			ref2base.put(refPosBC, refBase);
		}
	
		dest.addBaseCall(winArrestPos, refPosBC, base);
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		baseCallRegionDataCache.addData(data, coordinate);
		
		final int winArrestPos = getCoordinateController().convert2windowPosition(coordinate);

		if (refPos2BaseCallCountExtractor != null) {
			return;
		}

		if (lrtArrestCountExtractor != null) {
			final LRTarrestCount lrtArrestCount = lrtArrestCountExtractor.getLRTarrestCountExtractor(data);
			final RTarrestCount rtArrestCount 	= lrtArrestCount.getRTarrestCount();
			
			rtArrestCount.setReadStart(start.getArrestCount(winArrestPos));
			rtArrestCount.setReadEnd(end.getArrestCount(winArrestPos));
			final int inner = coverageWithoutBQ[winArrestPos] - 
					(rtArrestCount.getReadStart() + rtArrestCount.getReadEnd());
			rtArrestCount.setReadInternal(inner);
			
			boolean invert = coordinate.getStrand() == STRAND.REVERSE ? true : false;
			
			int arrest 	= 0;
			int through = 0;
			
			switch (libraryType) {
			
			case UNSTRANDED:
				arrest 	+= rtArrestCount.getReadStart();
				arrest 	+= rtArrestCount.getReadEnd();
				through += coverageWithoutBQ[winArrestPos] - 
						(rtArrestCount.getReadStart() + rtArrestCount.getReadEnd());

				add(start, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest());
				add(end, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest());
				break;

			case FR_FIRSTSTRAND:
				arrest 	+= rtArrestCount.getReadEnd();
				through += coverageWithoutBQ[winArrestPos] - 
						(rtArrestCount.getReadEnd());

				add(end, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest());
				break;

			case FR_SECONDSTRAND:
				arrest 	+= rtArrestCount.getReadStart();
				through += coverageWithoutBQ[winArrestPos] - 
						(rtArrestCount.getReadStart());

				add(start, winArrestPos, invert, lrtArrestCount.getRefPos2bc4arrest());
				break;
				
			case MIXED:
				throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
			}

			rtArrestCount.setReadArrest(arrest);
			rtArrestCount.setReadThrough(through);
		}
	}

	private void add(final ArrestPos2RefPos2BaseCallCount src, 
			final int winArrestPos, boolean invert, 
			final RefPos2BaseCallCount dest) {
		
		if (src.getArrestCount(winArrestPos) == 0) {
			return;
		}

		for (final int refPos : src.getRef2bc(winArrestPos).getRefPos()) {
			byte refBase = 'N';

			// check if we are outside of window
			final int baseCallWindowPosition = getCoordinateController().convert2windowPosition(refPos);
			if (baseCallWindowPosition < 0) {
				refBase = ref2base.get(refPos);
			} else {
				// get reference base from common storage within window
				refBase = getCoordinateController().getReferenceProvider().getReference(baseCallWindowPosition);
			}
			if (refBase == 'N') {
				continue;
			}

			final BaseCallCount baseCallCount = new ArrayBaseCallCount();
			baseCallCount.add(src.getRef2bc(winArrestPos).getBaseCallCount(refPos));
			baseCallCount.set(Base.valueOf(refBase), 0); // FIXME really set to zero???
			if (invert) {
				baseCallCount.invert();
			}
			dest.add(refPos, refBase, baseCallCount);
		}
	}
	
	@Override
	public void clear() {
		start.clear();
		end.clear();

		Arrays.fill(coverageWithoutBQ, 0);
		int max = 100;
		if (ref2base.size() < max) {
			ref2base.clear();
		} else {
			ref2base = new HashMap<Integer, Byte>(max);
		}
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}

}
