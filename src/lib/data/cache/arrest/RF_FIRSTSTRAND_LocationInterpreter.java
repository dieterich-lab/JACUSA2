package lib.data.cache.arrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.Region;

public class RF_FIRSTSTRAND_LocationInterpreter 
implements LocationInterpreter {


	@Override
	public List<Region> getArrestRegions(SAMRecord record) {
		return Arrays.asList(getLastArrestRegion(record));
	}

	@Override
	public List<Region> getThroughRegions(SAMRecord record) {
		final int size = record.getAlignmentBlocks().size();
		final List<Region> regions = new ArrayList<>(size);
		regions.addAll(getThroughRegion(0, size - 1, record));
		regions.add(getLastThroughRegion(record));
		return regions;
	}
	
}
