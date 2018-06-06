package lib.data.cache;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.basecall.BaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.basecall.RTarrestCountExtractor;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.count.BaseCallCount;
import lib.data.count.RTarrestCount;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public class ArrestThroughDataCache<T extends AbstractData> 
extends AbstractDataAdder<T> 
implements RecordWrapperDataCache<T> {

	// can be null
	private final RTarrestCountExtractor<T> rtCountExtractor;
	private final BaseCallCountExtractor<T> baseCallCountExtractor;
	private final BaseCallCountExtractor<T> arrestBaseCallCountExtractor;
	private final BaseCallCountExtractor<T> throughBaseCallCountExtractor;
	
	private final BaseCallAdder<T> startBaseCallAdder;
	private final ValidatedRegionDataCache<T> startValidated;
	
	private final BaseCallAdder<T> wholeBaseCallAdder;
	private final ValidatedRegionDataCache<T> wholeValidated;
	
	private final BaseCallAdder<T> endBaseCallAdder;
	private final ValidatedRegionDataCache<T> endValidated;

	public ArrestThroughDataCache(
			final RTarrestCountExtractor<T> rtCountExtractor,
			final BaseCallCountExtractor<T> baseCallCountExtractor,
			final BaseCallCountExtractor<T> arrestBaseCallCountExtractor,
			final BaseCallCountExtractor<T> throughBaseCallCountExtractor,
			final LIBRARY_TYPE libraryType,
			final byte minBASQ, 
			final CoordinateController coordinateController) {
		
		super(coordinateController);

		this.rtCountExtractor				= rtCountExtractor;
		this.baseCallCountExtractor 		= baseCallCountExtractor;
		this.arrestBaseCallCountExtractor 	= arrestBaseCallCountExtractor;
		this.throughBaseCallCountExtractor 	= throughBaseCallCountExtractor;

		// add validators
		final List<BaseCallValidator> validators = new ArrayList<BaseCallValidator>();
		validators.add(new DefaultBaseCallValidator());
		if (minBASQ > 0) {
			validators.add(new MinBASQBaseCallValidator(minBASQ));
		}
		startValidated		= new ValidatedRegionDataCache<T>(coordinateController);
		wholeValidated		= new ValidatedRegionDataCache<T>(coordinateController);
		endValidated 		= new ValidatedRegionDataCache<T>(coordinateController);
		for (final BaseCallValidator validator : validators) {
			startValidated.addValidator(validator);
			wholeValidated.addValidator(validator);
			endValidated.addValidator(validator);
		}
	
		// add adders
		switch (libraryType) {

		case UNSTRANDED:
			startBaseCallAdder = new ArrayBaseCallAdder<T>(arrestBaseCallCountExtractor, coordinateController);
			startValidated.addAdder(startBaseCallAdder);
			
			endBaseCallAdder = new ArrayBaseCallAdder<T>(arrestBaseCallCountExtractor, coordinateController);
			endValidated.addAdder(endBaseCallAdder);
			break;

		case FR_FIRSTSTRAND:
			startBaseCallAdder = new ArrayBaseCallAdder<T>(null, coordinateController);
			startValidated.addAdder(startBaseCallAdder);
			
			endBaseCallAdder = new ArrayBaseCallAdder<T>(arrestBaseCallCountExtractor, coordinateController);
			endValidated.addAdder(endBaseCallAdder);
			break;

		case FR_SECONDSTRAND:
			startBaseCallAdder = new ArrayBaseCallAdder<T>(arrestBaseCallCountExtractor, coordinateController);
			startValidated.addAdder(startBaseCallAdder);
			
			endBaseCallAdder = new ArrayBaseCallAdder<T>(null, coordinateController);
			endValidated.addAdder(endBaseCallAdder);
			break;
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
		
		wholeBaseCallAdder = new ArrayBaseCallAdder<T>(baseCallCountExtractor, coordinateController);
		wholeValidated.addAdder(wholeBaseCallAdder);
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		final AlignmentBlock first = record.getAlignmentBlocks().get(0);
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		
		final int alignmentStartWindowPosition = getCoordinateController().convert2windowPosition(first.getReferenceStart());
		if (alignmentStartWindowPosition >= 0) {
			startValidated.addRegion(first.getReferenceStart(), first.getReadStart() - 1, 1, recordWrapper);
		}
		
		final int alignmentEndWindowPosition = getCoordinateController().convert2windowPosition(last.getReferenceStart());
		if (alignmentEndWindowPosition >= 0) {
			final int length = last.getLength();
			endValidated.addRegion(last.getReferenceStart() + length - 1, last.getReadStart() + length - 2, 1, recordWrapper);
		}

		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			wholeValidated.addRegion(block.getReferenceStart(), block.getReadStart() - 1, block.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		startValidated.addData(data, coordinate);
		endValidated.addData(data, coordinate);
		wholeValidated.addData(data, coordinate);
		
		final int windowPosition 		= getCoordinateController().convert2windowPosition(coordinate);
		// through = whole - arrest
		final BaseCallCount throughBC 	= baseCallCountExtractor.getBaseCallCount(data).copy();
		if (throughBC.getCoverage() == 0) {
			// not covered at all
			return;
		}
		throughBC.substract(arrestBaseCallCountExtractor.getBaseCallCount(data));
		throughBaseCallCountExtractor.getBaseCallCount(data).add(throughBC);

		final RTarrestCount rtCount = rtCountExtractor.getRTcount(data);
		rtCount.setReadStart(startBaseCallAdder.getCoverage(windowPosition));
		rtCount.setReadEnd(endBaseCallAdder.getCoverage(windowPosition));
		rtCount.setReadInternal(wholeBaseCallAdder.getCoverage(windowPosition) - 
				startBaseCallAdder.getCoverage(windowPosition) - 
				endBaseCallAdder.getCoverage(windowPosition));
		rtCount.setReadArrest(arrestBaseCallCountExtractor.getBaseCallCount(data).getCoverage());
		rtCount.setReadThrough(throughBaseCallCountExtractor.getBaseCallCount(data).getCoverage());
	}

	@Override
	public void clear() {
		startValidated.clear();
		wholeValidated.clear();
		endValidated.clear();
	}

}
