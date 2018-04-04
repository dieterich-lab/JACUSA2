package lib.data.cache.lrtarrest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.AbstractRegionDataCache;
import lib.data.cache.region.UniqueRegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public abstract class AbstractUniqueLRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractRegionDataCache<T> 
implements UniqueRegionDataCache<T> {

	private final LIBRARY_TYPE libraryType;
	
	private final BaseCallConfig baseCallConfig;

	private final byte minBASQ;
	
	// readStart of read -> reference position of mismatch position -> baseCallCount of mismatch position
	private final List<Map<Integer, BaseCallCount>> readStart2ref2bcs;
	private final List<Map<Integer, BaseCallCount>> readEnd2ref2bcs;
	
	// readStart[i] -> positions in readStart2ref2baseCalls[i][]
	private final List<Set<Integer>> readStart2ref;
	private final List<Set<Integer>> readEnd2ref;

	private final Set<Integer> windowPositions;

	private boolean[] visited;
	
	public AbstractUniqueLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.libraryType = libraryType;

		this.minBASQ = minBASQ;
		
		this.baseCallConfig = baseCallConfig;
	
		final int n = coordinateController.getActiveWindowSize();

		readStart2ref2bcs 	= new ArrayList<Map<Integer,BaseCallCount>>(n);
		readEnd2ref2bcs 	= new ArrayList<Map<Integer,BaseCallCount>>(n);
		
		readStart2ref 		= new ArrayList<Set<Integer>>(n);
		readEnd2ref 		= new ArrayList<Set<Integer>>(n);
		windowPositions				= new HashSet<Integer>(n);
		
		final int sites = 10;
		for (int i = 0; i < n; ++i) {
			readStart2ref2bcs.add(new HashMap<Integer, BaseCallCount>(100));
			readEnd2ref2bcs.add(new HashMap<Integer, BaseCallCount>(100));

			readStart2ref.add(new HashSet<Integer>(sites));
			readEnd2ref.add(new HashSet<Integer>(sites));
		}
	}

	@Override
	public void resetVisited(final SAMRecordWrapper recordWrapper) {
		visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
	}
	
	@Override
	public void addRecordWrapperRegion(int referencePosition, int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
	
		final SAMRecord record = recordWrapper.getSAMRecord();
		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			final int guardedReferencePosition = windowPositionGuard.getReferencePosition() + j;
			final int guardedReadPosition = windowPositionGuard.getReadPosition() + j;

			/* TODO remove
			if (guardedReadPosition == -1) {
				int i = 0;
				++i;
			}
			*/
			
			// check baseCall is not "N"
			final byte bc = record.getReadBases()[guardedReadPosition];
			final int baseIndex = baseCallConfig.getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}

			// check baseCall quality
			final byte bq = record.getBaseQualities()[windowPositionGuard.getReadPosition() + j];
			if (bq < minBASQ) {
				continue;
			}
			
			if (windowPosition1 >= 0) {
				add(windowPosition1, guardedReadPosition, guardedReferencePosition, baseIndex, readStart2ref2bcs);
			}

			if (windowPosition2 >= 0) {
				add(windowPosition2, guardedReadPosition, guardedReferencePosition, baseIndex, readEnd2ref2bcs);
			}
		}
	}

	protected void add(final int windowPosition, final int readPosition, final int reference, 
			final int baseIndex, final List<Map<Integer, BaseCallCount>> win2refBc) {

		if (visited[readPosition]) {
			return;
		}
		
		windowPositions.add(windowPosition);
		Map<Integer, BaseCallCount> ref2bc = win2refBc.get(windowPosition);
		if (! ref2bc.containsKey(reference)) {
			ref2bc.put(reference, new BaseCallCount());
		}
		ref2bc.get(reference).increment(baseIndex);

		visited[readPosition] = true;
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);

		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE) {
			invert = true;
		}

		final Map<Integer, BaseCallCount> ref2bc = new HashMap<Integer, BaseCallCount>();
				
		switch (libraryType) {

		case UNSTRANDED:
			add(windowPosition, invert, readStart2ref2bcs, readStart2ref,
					ref2bc);
			break;

		case FR_FIRSTSTRAND:
			add(windowPosition, invert, readEnd2ref2bcs, readEnd2ref,
					ref2bc);
			break;

		case FR_SECONDSTRAND:
			add(windowPosition, invert, readStart2ref2bcs, readStart2ref,
					ref2bc);
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		addRefPos2bc(ref2bc, data);
	}
	
	protected abstract void addRefPos2bc(Map<Integer, BaseCallCount> ref2bc, T Data);
	
	private void add(final int windowPosition, final boolean invert,
			final List<Map<Integer, BaseCallCount>> win2ref2bc,
			final List<Set<Integer>> win2refPositions,
			final Map<Integer, BaseCallCount> lrtArrestRef2bc) {

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
			
			if (!  lrtArrestRef2bc.containsKey(referencePosition)) {
				lrtArrestRef2bc.put(referencePosition, new BaseCallCount());
			}
			lrtArrestRef2bc.get(referencePosition).add(baseCallCount);
		}
	}
	
	@Override
	public void clear() {
		for (int windowPosition : windowPositions) {
			readStart2ref2bcs.get(windowPosition).clear();
			readEnd2ref2bcs.get(windowPosition).clear();
			
			readStart2ref.get(windowPosition).clear();
			readEnd2ref.get(windowPosition).clear();
		}
		windowPositions.clear();
	}

}
