package lib.data.cache.arrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.Region;

public class UnstrandedLocationInterpreter implements LocationInterpreter {
	
	@Override
	public List<Region> getArrestRegions(SAMRecord record) {
		return Arrays.asList(
				getFirstArrestRegion(record),
				getLastArrestRegion(record));
	}
	
	@Override
	public List<Region> getThroughRegions(SAMRecord record) {
		final int size = record.getAlignmentBlocks().size();
		final List<Region> regions = new ArrayList<>(size);
		
		if (size == 1) {
			regions.add(getInnerThroughRegion(record));
		} else {
			regions.add(getFirstThroughRegion(record));
			regions.addAll(getThroughRegion(1, size - 2, record));
			regions.add(getLastThroughRegion(record));
		}

		return regions;
	}

}
