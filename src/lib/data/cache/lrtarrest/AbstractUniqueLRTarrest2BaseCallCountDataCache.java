package lib.data.cache.lrtarrest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.UniqueRegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public abstract class AbstractUniqueLRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractLRTarrest2BaseCallCountDataCache<T> 
implements UniqueRegionDataCache<T> {

	private boolean[] visited;
	
	public AbstractUniqueLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(libraryType, minBASQ, baseCallConfig, coordinateController);
	}

	@Override
	public void resetVisited(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}

	protected boolean add(final int windowPosition, final int readPosition, final int reference, 
			final int baseIndex, final List<Map<Integer, BaseCallCount>> win2refBc) {

		if (visited[readPosition]) {
			return false;
		}

		visited[readPosition] = true;
		return super.add(windowPosition, readPosition, reference, baseIndex, win2refBc);
	}

	protected void add(final int windowPosition, final boolean invert,
			final List<Map<Integer, BaseCallCount>> win2ref2bc,
			final List<Set<Integer>> win2refPositions,
			final Map<Integer, BaseCallCount> dest) {

		if (windowPosition < 0 && windowPosition >= win2ref2bc.size()) {
			return;
		}
		
		final Set<Integer> refPositions = win2refPositions.get(windowPosition);
		final Map<Integer, BaseCallCount> ref2bc = win2ref2bc.get(windowPosition);
		
		for (final int referencePosition : refPositions) {
			final BaseCallCount baseCallCount = ref2bc.get(referencePosition);
			if (invert) {
				baseCallCount.invert();
			}
			
			if (! dest.containsKey(referencePosition)) {
				dest.put(referencePosition, new BaseCallCount());
			}
			dest.get(referencePosition).add(baseCallCount);
		}
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);

		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE) {
			invert = true;
		}

		final Map<Integer, BaseCallCount> ref2bc = getRefPos2bc(data);
		
		switch (getLibraryType()) {

		case UNSTRANDED:
			add(windowPosition, invert, getReadStart2ref2bcs(), getReadStart2ref(), ref2bc);
			break;

		case FR_FIRSTSTRAND:
			add(windowPosition, invert, getReadEnd2ref2bcs(), getReadEnd2ref(), ref2bc);
			break;

		case FR_SECONDSTRAND:
			add(windowPosition, invert, getReadStart2ref2bcs(), getReadStart2ref(), ref2bc);
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + getLibraryType().toString());
		}
	}
	
}
