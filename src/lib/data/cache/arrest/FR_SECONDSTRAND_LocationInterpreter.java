package lib.data.cache.arrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.Region;

public class FR_SECONDSTRAND_LocationInterpreter 
implements LocationInterpreter {
	

	@Override
	public List<Region> getArrestRegions(SAMRecord record) {
		return Arrays.asList(getFirstArrestRegion(record));
	}
	
	@Override
	public List<Region> getThroughRegions(SAMRecord record) {
		final int size = record.getAlignmentBlocks().size();
		final List<Region> regions = new ArrayList<>(size);
		regions.add(getFirstThroughRegion(record));
		regions.addAll(getThroughRegion(1, size, record));
		return regions;
	}

}
