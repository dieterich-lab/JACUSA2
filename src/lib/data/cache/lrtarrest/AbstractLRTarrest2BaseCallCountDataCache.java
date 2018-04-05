package lib.data.cache.lrtarrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallCount;
import lib.data.LRTarrestCount;
import lib.data.RTarrestCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.AbstractDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

public abstract class AbstractLRTarrest2BaseCallCountDataCache<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount> 
extends AbstractDataCache<T> {

	private final LIBRARY_TYPE libraryType;
	
	private final BaseCallConfig baseCallConfig;

	private final byte minBASQ;
	
	// readStart of read -> reference position of mismatch position -> baseCallCount of mismatch position
	private final List<Map<Integer, BaseCallCount>> readStart2ref2bcs;
	private final List<Map<Integer, BaseCallCount>> readEnd2ref2bcs;
	
	private final Map<Integer, Byte> reference;
	
	// readStart[i] -> positions in readStart2ref2baseCalls[i][]
	private final List<Set<Integer>> readStart2ref;
	private final List<Set<Integer>> readEnd2ref;

	private final int[] readStartCount;
	private final int[] readEndCount;
	private final int[] coverageWithBQ;
	private final int[] coverageWithoutBQ;

	private final int[][] baseCalls;
	
	private final Set<Integer> windowPositions;
	
	public AbstractLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.libraryType = libraryType;

		this.minBASQ = minBASQ;
		
		this.baseCallConfig = baseCallConfig;
		
		final int n 		= coordinateController.getActiveWindowSize();
		readStartCount 		= new int[n];
		readEndCount 		= new int[n];
		coverageWithBQ		= new int[n];
		coverageWithoutBQ	= new int[n];

		readStart2ref2bcs 	= new ArrayList<Map<Integer,BaseCallCount>>(n);
		readEnd2ref2bcs 	= new ArrayList<Map<Integer,BaseCallCount>>(n);
		
		readStart2ref 		= new ArrayList<Set<Integer>>(n);
		readEnd2ref 		= new ArrayList<Set<Integer>>(n);
		windowPositions		= new HashSet<Integer>(n);
		
		reference			= new HashMap<Integer, Byte>(n);
		
		final int mutations = 5;
		for (int i = 0; i < n; ++i) {
			readStart2ref2bcs.add(new HashMap<Integer, BaseCallCount>(100));
			readEnd2ref2bcs.add(new HashMap<Integer, BaseCallCount>(100));

			readStart2ref.add(new HashSet<Integer>(mutations));
			readEnd2ref.add(new HashSet<Integer>(mutations));
		}

		baseCalls 			= new int[n][baseCallConfig.getBases().length];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();

		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		if (windowPosition1 >= 0) {
			readStartCount[windowPosition1]++;
		}

		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());
		if (windowPosition2 >= 0) {
			readEndCount[windowPosition2]++;
		}

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
		
		final int windowPosition = getCoordinateController().convert2windowPosition(referencePosition);
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		int windowPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int windowPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < length; ++j) {
			final int tmpReferencePosition 	= referencePosition + j;
			final int tmpWindowPosition 	= windowPosition + j;
			final int tmpReadPosition 		= readPosition + j;

			byte refBase 					= 'N';

			// check baseCall is not "N"
			final byte bc = record.getReadBases()[tmpReadPosition];
			final int baseIndex = baseCallConfig.getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}

			if (tmpWindowPosition >= 0 && tmpWindowPosition < getCoordinateController().getActiveWindowSize()) {
				// ignore base quality here 
				coverageWithoutBQ[tmpWindowPosition]++;
	
				// check baseCall quality
				final byte bq = record.getBaseQualities()[tmpReadPosition];
				if (bq < minBASQ) {
					continue;
				}

				// count base call
				baseCalls[tmpWindowPosition][baseIndex]++;

				// get reference base from common storage within window
				refBase = getCoordinateController().getReferenceProvider().getReference(tmpWindowPosition);
			} else {
				// get reference base from read
				refBase = recordWrapper.getReference()[tmpReadPosition];
			}

			// position has a non-ref base call
			boolean nonRef = false;
			if (baseCallConfig.getBaseIndex(refBase) >= 0 && refBase != bc) {
				nonRef = true;
			}
			
			// mismatch
			if (windowPosition1 >= 0) {
				if (add(windowPosition1, tmpReadPosition, tmpReferencePosition, baseIndex, readStart2ref2bcs)) {
					if (nonRef) {
						readStart2ref.get(windowPosition1).add(tmpReferencePosition);
						if (! reference.containsKey(tmpReferencePosition)) {
							reference.put(tmpReferencePosition, refBase);
						}
					}
				}
			}
				
			if (windowPosition2 >= 0) {
				if (add(windowPosition2, tmpReadPosition, tmpReferencePosition, baseIndex, readEnd2ref2bcs)) {
					if (nonRef) {
						readEnd2ref.get(windowPosition2).add(tmpReferencePosition);
						if (! reference.containsKey(tmpReferencePosition)) {
							reference.put(tmpReferencePosition, refBase);
						}
					}
				}
			}
		}
	}

	protected boolean add(final int windowPosition, final int readPosition, final int reference, 
			final int baseIndex, final List<Map<Integer, BaseCallCount>> win2refBc) {

		windowPositions.add(windowPosition);
		Map<Integer, BaseCallCount> ref2bc = win2refBc.get(windowPosition);
		if (! ref2bc.containsKey(reference)) {
			ref2bc.put(reference, new BaseCallCount());
		}
		ref2bc.get(reference).increment(baseIndex);
		
		return true;
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);

		final LRTarrestCount lrtArrestCount = data.getLRTarrestCount();
		final RTarrestCount readArrestCount = lrtArrestCount.getRTarrestCount();
		
		readArrestCount.setReadStart(readStartCount[windowPosition]);
		readArrestCount.setReadEnd(readEndCount[windowPosition]);

		// TODO TEST if coverage is calculated correctly
		final int internal = coverageWithoutBQ[windowPosition] - 
				(readArrestCount.getReadStart() + readArrestCount.getReadEnd());
		readArrestCount.setReadInternal(internal);

		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE) {
			invert = true;
		}
		
		int arrest = 0;
		int through = 0;

		final Map<Integer, BaseCallCount> ref2bc = getRefPos2bc(data);
		
		switch (libraryType) {

		case UNSTRANDED:
			arrest 	+= readArrestCount.getReadStart();
			arrest 	+= readArrestCount.getReadEnd();
			through += readArrestCount.getReadInternal();
			
			add(windowPosition, invert, readStart2ref2bcs, readStart2ref,
					lrtArrestCount, ref2bc);
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= readArrestCount.getReadEnd();
			through += readArrestCount.getReadInternal();
			
			add(windowPosition, invert, readEnd2ref2bcs, readEnd2ref,
					lrtArrestCount, ref2bc);
			break;

		case FR_SECONDSTRAND:
			arrest 	+= readArrestCount.getReadStart();
			through += readArrestCount.getReadInternal();
			
			add(windowPosition, invert, readStart2ref2bcs, readStart2ref,
					lrtArrestCount, ref2bc);
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		for (int refPosition : ref2bc.keySet()) {
			if (! lrtArrestCount.getReference().containsKey(refPosition)) {
				lrtArrestCount.getReference().put(refPosition, reference.get(refPosition));
			}
		}
		
		// FIXME this should be done somewhere else
		data.setReferenceBase(getCoordinateController().getReferenceProvider().getReference(windowPosition));

		// add base call data
		System.arraycopy(baseCalls[windowPosition], 0, 
				data.getBaseCallCount().getBaseCallCount(), 0, baseCallConfig.getBases().length);
		if (invert) {
			data.getBaseCallCount().invert();
		}

		readArrestCount.setReadArrest(arrest);
		readArrestCount.setReadThrough(through);
	}
	
	protected abstract Map<Integer, BaseCallCount> getRefPos2bc(T Data);
	
	protected void add(final int windowPosition, final boolean invert,
			final List<Map<Integer, BaseCallCount>> win2ref2bc,
			final List<Set<Integer>> win2refPositions,
			final LRTarrestCount lrtArrestCount,
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

			lrtArrestCount.add(lrtArrestRef2bc, referencePosition, baseCallCount);
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
		reference.clear();
		
		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);
		Arrays.fill(coverageWithBQ, 0);
		Arrays.fill(coverageWithoutBQ, 0);
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
	}

	protected List<Map<Integer, BaseCallCount>> getReadStart2ref2bcs() {
		return readStart2ref2bcs;
	}
	
	protected List<Map<Integer, BaseCallCount>> getReadEnd2ref2bcs() {
		return readEnd2ref2bcs;
	}

	protected List<Set<Integer>> getReadStart2ref() {
		return readStart2ref;
	}
	
	protected List<Set<Integer>> getReadEnd2ref() {
		return readEnd2ref;
	}

	protected Map<Integer, Byte> getReference() {
		return reference;
	}
	
	public int getReadStartCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readStartCount[windowPosition];
	}

	public int getReadEndCount(final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		return readEndCount[windowPosition];
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
}
