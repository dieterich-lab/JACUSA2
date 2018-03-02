package lib.data.cache;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

import htsjdk.samtools.AlignmentBlock;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

public class UniqueRef2BaseCallDataCache<T extends AbstractData & hasReferenceBase & hasLRTarrestCount> 
extends AbstractUniqueDataCache<T> {
	
	private final LinkedRT2BaseChangeDataCache<T> cache; // TODO name
	
	public UniqueRef2BaseCallDataCache(final LIBRARY_TYPE libraryType,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(coordinateController);

		cache = new LinkedRT2BaseChangeDataCache<T>(libraryType, baseCallConfig, coordinateController);
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			cache.parseTODO(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength(), recordWrapper);
		}
	}

	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		cache.parseTODO(referencePosition, readPosition, length, recordWrapper);
	}
	
	protected void add(final int windowPosition, final int readPosition, final int reference, 
			final int baseIndex, final List<List<SimpleEntry<Integer, Integer>>> win2refBc) {
		if (! getVisited()[readPosition]) {
			cache.add(windowPosition, readPosition, reference, baseIndex, win2refBc);
			getVisited()[readPosition] = true;
		}
	}

	@Override
	public void addData(T data, Coordinate coordinate) {
		cache.addData(data, coordinate);
	}
	
	@Override
	public void clear() {
		cache.clear();
	}
	
}
