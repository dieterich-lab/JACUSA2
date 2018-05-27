package lib.data.cache;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.data.AbstractData;
import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.basecall.RTarrestCountExtractor;
import lib.data.cache.record.RecordDataCache;
import lib.data.cache.region.ArrayBaseCallRegionDataCache;
import lib.data.count.BaseCallCount;
import lib.data.count.RTarrestCount;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public class ArrestThroughDataCache<T extends AbstractData> 
extends AbstractDataCache<T> 
implements RecordDataCache<T> {

	// can be null
	private final RTarrestCountExtractor<T> rtCountExtractor;
	private final BaseCallCountExtractor<T> baseCallCountExtractor;
	private final BaseCallCountExtractor<T> arrestBaseCallCountExtractor;
	private final BaseCallCountExtractor<T> throughBaseCallCountExtractor;
	
	private final LIBRARY_TYPE libraryType;
	
	private final ArrayBaseCallRegionDataCache<T> startBC;
	private final ArrayBaseCallRegionDataCache<T> wholeBC;
	private final ArrayBaseCallRegionDataCache<T> endBC;

	public ArrestThroughDataCache(
			final RTarrestCountExtractor<T> rtCountExtractor,
			final BaseCallCountExtractor<T> baseCallCountExtractor,
			final BaseCallCountExtractor<T> arrestBaseCallCountExtractor,
			final BaseCallCountExtractor<T> throughBaseCallCountExtractor,
			final LIBRARY_TYPE libraryType,
			final int maxDepth, final byte minBASQ, 
			final CoordinateController coordinateController) {
		
		super(coordinateController);

		this.rtCountExtractor				= rtCountExtractor;
		this.baseCallCountExtractor 		= baseCallCountExtractor;
		this.arrestBaseCallCountExtractor 	= arrestBaseCallCountExtractor;
		this.throughBaseCallCountExtractor 	= throughBaseCallCountExtractor;
		
		this.libraryType = libraryType;
		
		startBC	= new ArrayBaseCallRegionDataCache<T>(maxDepth, minBASQ, coordinateController);
		wholeBC	= new ArrayBaseCallRegionDataCache<T>(maxDepth, minBASQ, coordinateController);
		endBC 	= new ArrayBaseCallRegionDataCache<T>(maxDepth, minBASQ, coordinateController);
	}

	@Override
	public void addRecord(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		final AlignmentBlock first = record.getAlignmentBlocks().get(0);
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		
		int windowPosition1 = getCoordinateController().convert2windowPosition(first.getReferenceStart());
		if (windowPosition1 >= 0) {
			startBC.addRegion(first.getReferenceStart(), first.getReadStart() - 1, 1, recordWrapper);
		}
		
		int windowPosition2 = getCoordinateController().convert2windowPosition(last.getReferenceStart());
		if (windowPosition2 >= 0) {
			final int length = last.getLength();
			endBC.addRegion(last.getReferenceStart() + length - 1, last.getReadStart() + length - 2, 1, recordWrapper);
		}

		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			wholeBC.addRegion(block.getReferenceStart(), block.getReadStart() - 1, block.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (baseCallCountExtractor != null) {
			final BaseCallCount baseCallCount = getBaseCallCount(windowPosition, libraryType);
			add(windowPosition, coordinate.getStrand(), data, baseCallCount, baseCallCountExtractor);
		}

		BaseCallCount arrestBC = null;
		if (arrestBaseCallCountExtractor != null) {
			arrestBC = getArrestBaseCallCount(windowPosition, libraryType);
			add(windowPosition, coordinate.getStrand(), data, arrestBC, arrestBaseCallCountExtractor);
		}

		if (throughBaseCallCountExtractor != null) {
			BaseCallCount throughBC = null;
			if (arrestBC != null) {
				throughBC = getThroughBaseCallCount(windowPosition, libraryType, arrestBC);
			} else {
				throughBC = getThroughBaseCallCount(windowPosition, libraryType);
			}
			add(windowPosition, coordinate.getStrand(), data, throughBC, throughBaseCallCountExtractor);
		}

		if (rtCountExtractor != null) {
			final RTarrestCount rtCount = rtCountExtractor.getRTcount(data);
			rtCount.setReadStart(startBC.getCoverage()[windowPosition]);
			rtCount.setReadEnd(endBC.getCoverage()[windowPosition]);
			rtCount.setReadInternal(wholeBC.getCoverage()[windowPosition] - 
					startBC.getCoverage()[windowPosition] - 
					endBC.getCoverage()[windowPosition]);
			if (arrestBaseCallCountExtractor != null) {
				rtCount.setReadArrest(arrestBaseCallCountExtractor.getBaseCallCount(data).getCoverage());
			}
			if (throughBaseCallCountExtractor != null) {
				rtCount.setReadThrough(throughBaseCallCountExtractor.getBaseCallCount(data).getCoverage());
			}
		}
	}
	
	public void add(final int windowPosition, final STRAND strand, final T data, final BaseCallCount src, final BaseCallCountExtractor<T> extractor) {
		final BaseCallCount dest = extractor.getBaseCallCount(data);
		dest.add(src);
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}		
	} 
	
	protected BaseCallCount getBaseCallCount(final int windowPosition, final LIBRARY_TYPE libraryType) {
		return new ArrayBaseCallCount(getWholeBC().getBaseCalls()[windowPosition]);
	}
		
	protected BaseCallCount getArrestBaseCallCount(final int windowPosition, final LIBRARY_TYPE libraryType) {
		final BaseCallCount arrestBC = new ArrayBaseCallCount();

		switch (libraryType) {

		case UNSTRANDED:
			arrestBC.add(new ArrayBaseCallCount(getStartBC().getBaseCalls()[windowPosition]));
			arrestBC.add(new ArrayBaseCallCount(getEndBC().getBaseCalls()[windowPosition]));
			break;

		case FR_FIRSTSTRAND:
			arrestBC.add(new ArrayBaseCallCount(getEndBC().getBaseCalls()[windowPosition]));
			
			break;

		case FR_SECONDSTRAND:
			arrestBC.add(new ArrayBaseCallCount(getStartBC().getBaseCalls()[windowPosition]));
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		return arrestBC;
	}

	protected BaseCallCount getThroughBaseCallCount(final int windowPosition, final LIBRARY_TYPE libraryType) {
		return getThroughBaseCallCount(windowPosition, libraryType, getArrestBaseCallCount(windowPosition, libraryType));
	}
	
	protected BaseCallCount getThroughBaseCallCount(final int windowPosition, final LIBRARY_TYPE libraryType, final BaseCallCount arrestBC) {
		final BaseCallCount throughBC = new ArrayBaseCallCount(getBaseCallCount(windowPosition, libraryType));
		throughBC.substract(arrestBC);
		return throughBC;
	}

	public ArrayBaseCallRegionDataCache<T> getStartBC() {
		return startBC;
	}
	
	public ArrayBaseCallRegionDataCache<T> getWholeBC() {
		return wholeBC;
	}
	
	public ArrayBaseCallRegionDataCache<T> getEndBC() {
		return endBC;
	}

	@Override
	public void clear() {
		startBC.clear();
		wholeBC.clear();
		endBC.clear();
	}

}
