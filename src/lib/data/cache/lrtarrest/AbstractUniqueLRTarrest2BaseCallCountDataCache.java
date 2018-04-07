package lib.data.cache.lrtarrest;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import java.util.List;
import java.util.Map;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.CoordinateUtil.STRAND;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.AbstractDataCache;
import lib.data.cache.region.UniqueRegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public abstract class AbstractUniqueLRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractDataCache<T> 
implements UniqueRegionDataCache<T> {

	private final LIBRARY_TYPE libraryType;
	private final BaseCallConfig baseCallConfig;
	private final byte minBASQ;

	private final LRTarrest2BaseCallCount start;
	private final LRTarrest2BaseCallCount end;
		
	private boolean[] visited;

	public AbstractUniqueLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.libraryType 	= libraryType;
		this.minBASQ 		= minBASQ;
		this.baseCallConfig = baseCallConfig;
		
		final int n = coordinateController.getActiveWindowSize();
		start 		= new LRTarrest2BaseCallCount(coordinateController, n);
		end 		= new LRTarrest2BaseCallCount(coordinateController, n);
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

		Map<Integer, BaseCallCount> ref2bc = win2refBc.get(windowPosition);
		if (! ref2bc.containsKey(reference)) {
			ref2bc.put(reference, new BaseCallCount());
		}
		ref2bc.get(reference).increment(baseIndex);
		
		visited[readPosition] = true;
		return true;
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int winArrestPos = getCoordinateController().convert2windowPosition(coordinate);

		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE) {
			invert = true;
		}

		final Map<Integer, BaseCallCount> ref2bc = getRefPos2bc(data);
		
		switch (getLibraryType()) {

		case UNSTRANDED:
			start.copyNonRef(winArrestPos, invert, ref2bc);
			break;

		case FR_FIRSTSTRAND:
			end.copyNonRef(winArrestPos, invert, ref2bc);
			break;

		case FR_SECONDSTRAND:
			start.copyNonRef(winArrestPos, invert, ref2bc);
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + getLibraryType().toString());
		}
	}
	
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final WindowPositionGuard windowPositionGuard = 
					getCoordinateController().convert(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength());

			addRecordWrapperRegion(windowPositionGuard.getReferencePosition(), 
					windowPositionGuard.getReadPosition(), 
					windowPositionGuard.getLength(), recordWrapper);
		}
	}

	public void addRecordWrapperRegion(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {
		
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < length; ++j) {
			final int tmpReferencePosition 	= referencePosition + j;
			final int tmpReadPosition 		= readPosition + j;

			// check baseCall is not "N"
			final byte bc = record.getReadBases()[tmpReadPosition];
			final int baseIndex = baseCallConfig.getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}

			final byte bq = record.getBaseQualities()[tmpReadPosition];
			if (bq < minBASQ) {
				continue;
			}
			
			add(windowPosition1, tmpReferencePosition, tmpReadPosition, baseIndex, recordWrapper, start);
			add(windowPosition2, tmpReferencePosition, tmpReadPosition, baseIndex, recordWrapper, end);
		}
	}

	private void add(final int windowPosition, final int referencePosition, final int readPosition, final int baseIndex, 
			final SAMRecordWrapper recordWrapper, LRTarrest2BaseCallCount dest) {

		if (windowPosition < 0) {
			return; 
		}

		if (visited[readPosition]) {
			return;
		}

		dest.addBaseCall(windowPosition, referencePosition, baseIndex);
		visited[readPosition] = true;
	}
	
	protected abstract Map<Integer, BaseCallCount> getRefPos2bc(T Data);
	
	@Override
	public void clear() {
		start.clear();
		end.clear();
	}

}
