package lib.data.cache;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import lib.util.coordinate.CoordinateController;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.UniqueRegionDataCache;

import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

// FIXME lord have mercy
public class UniqueRef2BaseCallDataCache<T extends AbstractData & hasReferenceBase & hasLRTarrestCount> 
extends LRTarrest2BaseChangeDataCache<T> implements UniqueRegionDataCache<T> {
	
	// private boolean[] visited;
	
	public UniqueRef2BaseCallDataCache(final LIBRARY_TYPE libraryType,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(libraryType, baseCallConfig, coordinateController);
	}
	
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		/*
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			// TODO cache.parseTODO(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
		*/
	}

	public void addRecordWrapperRegion(final int referencePosition, final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		// TODO cache.parseTODO(referencePosition, readPosition, length, recordWrapper);
	}
	
	protected void add(final int windowPosition, final int readPosition, final int reference, 
			final int baseIndex, final List<List<SimpleEntry<Integer, Integer>>> win2refBc) {
		
	}

	@Override
	public void resetVisited(final SAMRecordWrapper recordWrapper) {
		// visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}
	
}
