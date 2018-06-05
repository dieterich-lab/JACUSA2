package jacusa.filter.factory.distance.rtarrest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.data.AbstractData;
import lib.data.cache.extractor.basecall.ArrestBaseCallCountExtractor;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.has.filter.HasBaseCallCountFilterData;

public class RTarrestReadPositionDistanceFilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount & HasReferenceBase & HasBaseCallCountFilterData> 
extends AbstractRTarrestDistanceFilterFactory<T> {

	public RTarrestReadPositionDistanceFilterFactory() {
		super(Option.builder(Character.toString('B'))
				.desc("Filter potential false positive variants adjacent to read start/end in read through reads.")
				.build(),
				new ArrestBaseCallCountExtractor<T>(),
				6, 0.5);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(1);
		processRecords.add(new ProcessReadStartEnd(getDistance(), regionDataCache));
		return processRecords;
	}
	
}