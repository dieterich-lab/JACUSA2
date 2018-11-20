package lib.data.cache.arrest;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import lib.data.has.LibraryType;
import lib.util.coordinate.Region;

public interface LocationInterpreter {

	List<Region> getArrestRegions(SAMRecord record);
	List<Region> getThroughRegions(SAMRecord record);
	
	default boolean isArrest(SAMRecord record, int readPosition) {
		for (final Region region : getArrestRegions(record)) {
			if (region.getReadStart() == readPosition) {
				return true;
			}
		}
		return false;
	}
	
	
	static LocationInterpreter create(final LibraryType libraryType) {
		switch (libraryType) {

		case UNSTRANDED:
			return new UnstrandedLocationInterpreter();

		case RF_FIRSTSTRAND:
			return new RF_FIRSTSTRAND_LocationInterpreter();

		case FR_SECONDSTRAND:
			return new FR_SECONDSTRAND_LocationInterpreter();
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}
	}
	
	default Region getFirstArrestRegion(final SAMRecord record) {
		final AlignmentBlock first = record.getAlignmentBlocks().get(0);
		return new Region(first.getReferenceStart(), first.getReadStart() - 1, 1);
	}
	
	default Region getLastArrestRegion(final SAMRecord record) {
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		final int lastLength = last.getLength();
		return new Region(last.getReferenceStart() + lastLength - 1, last.getReadStart() + lastLength - 2, 1);
	}
	
	default Region getFirstThroughRegion(final SAMRecord record) {
		final AlignmentBlock first = record.getAlignmentBlocks().get(0);
		return new Region(
				first.getReferenceStart() + 1, 
				first.getReadStart(), 
				first.getLength() - 1);
	}
	
	default Region getInnerThroughRegion(final SAMRecord record) {
		final AlignmentBlock single = record.getAlignmentBlocks().get(0);
		return new Region(
				single.getReferenceStart() + 1, 
				single.getReadStart(), 
				single.getLength() - 2);
	}
	
	default Region getLastThroughRegion(final SAMRecord record) {
		final int size = record.getAlignmentBlocks().size();
		final AlignmentBlock last = record.getAlignmentBlocks().get(size - 1);
		return new Region(
				last.getReferenceStart(), 
				last.getReadStart() - 1, 
				last.getLength() - 1);
	}
	
	default List<Region> getThroughRegion(
			final int startIndex, 
			final int size, 
			final SAMRecord record) {
		
		final int endIndex = startIndex + size;
		final List<Region> regions = new ArrayList<>(size);
		for (int i = startIndex; i < endIndex; ++i) {
			final AlignmentBlock block = record.getAlignmentBlocks().get(i);
			regions.add(
					new Region(
						block.getReferenceStart(), 
						block.getReadStart() - 1, 
						block.getLength())); 
		}
		return regions;
	}
	
	
}
