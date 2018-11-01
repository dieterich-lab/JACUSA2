package lib.data.cache;

import lib.util.coordinate.Coordinate;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.basecall.ArrayBaseCallAdder;
import lib.data.adder.region.ValidatedRegionDataCache;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;
import lib.data.cache.region.isvalid.DefaultBaseCallValidator;
import lib.data.cache.region.isvalid.MinBASQBaseCallValidator;
import lib.data.has.LibraryType;

public abstract class AbstractRTarrestDataCache 
extends AbstractDataContainerAdder 
implements RecordWrapperDataCache {
	
	private final LibraryType libraryType;
	private final ValidatedRegionDataCache arrest;
	private final ValidatedRegionDataCache through;

	public AbstractRTarrestDataCache(
			final LibraryType libraryType,
			final byte minBASQ, 
			final SharedCache sharedCache) {
		
		super(sharedCache);

		this.libraryType = libraryType;
		
		// add validators
		final List<BaseCallValidator> validators = new ArrayList<BaseCallValidator>();
		validators.add(new DefaultBaseCallValidator());
		if (minBASQ > 0) {
			validators.add(new MinBASQBaseCallValidator(minBASQ));
		}
		arrest = new ValidatedRegionDataCache(sharedCache);
		arrest.addAdder(new ArrayBaseCallAdder(DataType.ARREST_BCC.getFetcher(), sharedCache));
		
		through = new ValidatedRegionDataCache(sharedCache);
		through.addAdder(new ArrayBaseCallAdder(DataType.THROUGH_BCC.getFetcher(), sharedCache));
		for (final BaseCallValidator validator : validators) {
			arrest.addValidator(validator);
			through.addValidator(validator);
		}
	}

	protected void addFirstAlignmentBlockToThrough(final SAMRecordWrapper recordWrapper) {
		final AlignmentBlock first = recordWrapper.getSAMRecord().getAlignmentBlocks().get(0);
		getThrough().addRegion(
				first.getReferenceStart() + 1, 
				first.getReadStart(), 
				first.getLength() - 1, 
				recordWrapper);
	}
	
	protected void addSingleAlignmentBlockToThrough(final SAMRecordWrapper recordWrapper) {
		final AlignmentBlock single = recordWrapper.getSAMRecord().getAlignmentBlocks().get(0);
		getThrough().addRegion(
				single.getReferenceStart() + 1, 
				single.getReadStart(), 
				single.getLength() - 2, 
				recordWrapper);
	}
	
	protected void addLastAlignmentBlockToThrough(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		getThrough().addRegion(
				last.getReferenceStart(), 
				last.getReadStart() - 1, 
				last.getLength() - 1, 
				recordWrapper);
	}
	
	protected void addFirstAlignmentBlockToArrest(final SAMRecordWrapper recordWrapper) {
		final AlignmentBlock first = recordWrapper.getSAMRecord().getAlignmentBlocks().get(0);
		getArrest().addRegion(
				first.getReferenceStart(), 
				first.getReadStart() - 1, 
				1, 
				recordWrapper);
	}
	
	protected void addLastAlignmentBlockToArrest(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		final int lastLength = last.getLength();
		getArrest().addRegion(
				last.getReferenceStart() + lastLength - 1, 
				last.getReadStart() + lastLength - 2, 
				1, 
				recordWrapper);
	}
	
	protected void addAlignmentBlockToThrough(
			final int startIndex, final int size, 
			final SAMRecordWrapper recordWrapper) {
		
		for (int i = startIndex; i < size; ++i) {
			final AlignmentBlock block = recordWrapper.getSAMRecord().getAlignmentBlocks().get(i);
			getThrough().addRegion(
					block.getReferenceStart(), 
					block.getReadStart() - 1, 
					block.getLength(), 
					recordWrapper);
		}
	}
	
	protected ValidatedRegionDataCache getArrest() {
		return arrest;
	}
	
	protected ValidatedRegionDataCache getThrough() {
		return through;
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		// store arrest base calls
		arrest.populate(container, coordinate);
		through.populate(container, coordinate);
	}

	@Override
	public void clear() {
		arrest.clear();
		through.clear();
	}
	
}
