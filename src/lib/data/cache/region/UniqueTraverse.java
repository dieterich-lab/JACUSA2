package lib.data.cache.region;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public class UniqueTraverse<T extends AbstractData> 
implements RestrictedRegionDataCache<T> {

	private final RestrictedRegionDataCache<T> dataCache;

	private boolean[] visited;
	
	private SAMRecord record;
	
	public UniqueTraverse(final RestrictedRegionDataCache<T> dataCache) {
		this.dataCache = dataCache;
	}
	
	@Override
	public void increment(final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality) {

		if (! visited[readPosition]) {
			dataCache.increment(windowPosition, readPosition, base, baseQuality);
			visited[readPosition] = true;
		}
	}
	
	private void resetVisited(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}

	@Override
	public void addRegion(int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper) {
		if (record != null && recordWrapper.getSAMRecord() != record) {
			resetVisited(recordWrapper);
		}
		dataCache.addRegion(referencePosition, readPosition, length, recordWrapper);
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		dataCache.addData(data, coordinate);
	}

	@Override
	public CoordinateController getCoordinateController() {
		return dataCache.getCoordinateController();
	}

	@Override
	public void clear() {
		dataCache.clear();
	}

	@Override
	public boolean isValid(int windowPosition, int readPosition, Base base, byte baseQuality) {
		return dataCache.isValid(windowPosition, readPosition, base, baseQuality);
	}
	
}
