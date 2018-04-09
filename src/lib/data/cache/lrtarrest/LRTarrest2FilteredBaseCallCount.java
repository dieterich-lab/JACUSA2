package lib.data.cache.lrtarrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.cli.options.BaseCallConfig;
import lib.data.BaseCallCount;
import lib.util.coordinate.CoordinateController;

public class LRTarrest2FilteredBaseCallCount {

	private static final int READ_LENGTH = 100; 
	
	private final CoordinateController coordinateController;
	
	private final Set<Integer> arrest;
	private final List<Set<Integer>> arrest2window;
	private final int[][][] arrest2window2bc; 
	
	// nonRef base call outside of window
	private final Map<Integer, Set<Integer>> arrest2ref;
	private final Map<Integer, Map<Integer, BaseCallCount>> arrest2ref2bc;
	
	public LRTarrest2FilteredBaseCallCount(CoordinateController coordinateController, int n) {
		this.coordinateController = coordinateController;
		
		arrest 				= new HashSet<Integer>(n);
		arrest2window 		= new ArrayList<Set<Integer>>(n);
		arrest2window2bc 	= new int[n][n][BaseCallConfig.BASES.length];
		for (int i = 0; i < n; ++i) {
			arrest2window.add(new HashSet<Integer>(READ_LENGTH));
		}

		arrest2ref			= new HashMap<Integer, Set<Integer>>();
		arrest2ref2bc		= new HashMap<Integer, Map<Integer,BaseCallCount>>();
	}
	
	public void addBaseCall(final int winArrestPos, final int refBCPos, 
			final int baseIndex) {

		arrest.add(winArrestPos);
		
		final int winBCPos = coordinateController.convert2windowPosition(refBCPos);
		if (winBCPos < 0) {
			_addBaseCallOutsideWindow(winArrestPos, refBCPos, baseIndex);
		} else {
			_addBaseCall(winArrestPos, refBCPos, baseIndex, winBCPos);
		}
	}

	public void addNonRefBaseCall(final int winArrestPos, final int refBCPos, 
			final int baseIndex) {

		final int winBCPos = coordinateController.convert2windowPosition(refBCPos);
		if (winBCPos < 0) {
			_addBaseCallOutsideWindow(winArrestPos, refBCPos, baseIndex);
		} else {
			_addBaseCall(winArrestPos, refBCPos, baseIndex, winBCPos);
		}
	}

	private void _addBaseCallOutsideWindow(final int winArrestPos, final int refBCPos, final int baseIndex) {
		if (! arrest2ref.containsKey(winArrestPos)) {
			arrest2ref.put(winArrestPos, new HashSet<Integer>(5));
		}
		arrest2ref.get(winArrestPos).add(refBCPos);

		if (! arrest2ref2bc.containsKey(winArrestPos)) {
			arrest2ref2bc.put(winArrestPos, new HashMap<Integer, BaseCallCount>(10));
		}

		if (! arrest2ref2bc.get(winArrestPos).containsKey(refBCPos)) {
			arrest2ref2bc.get(winArrestPos).put(refBCPos, new BaseCallCount());
		}
		
		arrest2ref2bc.get(winArrestPos).get(refBCPos).increment(baseIndex);
	}
	
	private void _addBaseCall(final int winArrestPos, final int refBCPos, 
			final int baseIndex, final int winBCPos) {

		arrest2window.get(winArrestPos).add(winBCPos);
		arrest2window2bc[winArrestPos][winBCPos][baseIndex]++;
	}
	
	public void copyAll(final int winArrestPos, final boolean invert, final Map<Integer, BaseCallCount> dest) {
		final Set<Integer> winRefPositions = arrest2window.get(winArrestPos);
		
		_copy(winArrestPos, invert, winRefPositions, dest);
		if (arrest2ref.containsKey(winArrestPos)) {
			_copyOutsideWindow(winArrestPos, invert, arrest2ref.get(winArrestPos), dest);
		}
	}
	
	private void _copy(final int winArrestPos, final boolean invert, 
			final Set<Integer> winRefPositions, final Map<Integer, BaseCallCount> dest) {

		for (final int winRefPosition : winRefPositions) {
			final BaseCallCount baseCallCount = new BaseCallCount(arrest2window2bc[winArrestPos][winRefPosition]);
			if (invert) {
				baseCallCount.invert();
			}

			final int refPosition = coordinateController.convert2referencePosition(winRefPosition);
			
			if (! dest.containsKey(refPosition)) {
				dest.put(refPosition, new BaseCallCount());
			}
			dest.get(refPosition).add(baseCallCount);
		}
	}

	private void _copyOutsideWindow(final int winArrestPos, final boolean invert, 
			final Set<Integer> refPositions, final Map<Integer, BaseCallCount> dest) {

		for (final int refPosition : refPositions) {
			final BaseCallCount baseCallCount = new BaseCallCount(arrest2ref2bc.get(winArrestPos).get(refPosition));
			if (invert) {
				baseCallCount.invert();
			}
			
			if (! dest.containsKey(refPosition)) {
				dest.put(refPosition, new BaseCallCount());
			}
			dest.get(refPosition).add(baseCallCount);
		}
	}

	public void print() {
		System.out.print("Window Arrest Position:");
		for (final int p : arrest) {
			System.out.print(" " + p);	
		}
		System.out.print("\n");
		System.out.print("Window Arrest 2 Window Position:\n");
		for (final int p : arrest) {
			System.out.print(" " + p + ":");
			for (final int q : arrest2window.get(p)) {
				System.out.print(" " + q);	
			}
			System.out.print("\n");
		}
		System.out.print("Window Arrest 2 Window Position 2 Base Call:\n");
		for (final int p : arrest) {
			for (final int q : arrest2window.get(p)) {
				System.out.print(" " + p + " -> " + q + " => (");
				for (final int bc : arrest2window2bc[p][q]) {
					System.out.print(" " + bc + " ");
				}
				System.out.print(")\n");
			}
			System.out.print("\n");
		}
	}
	
	public Set<Integer> getArrest() {
		return arrest;
	}

	public void clear() {
		for (final int arrestPos : arrest) {
			for (final int windowPos : arrest2window.get(arrestPos)) {
				Arrays.fill(arrest2window2bc[arrestPos][windowPos], 0);
			}
			arrest2window.get(arrestPos).clear();
		}
		arrest.clear();
		
		// TODO test
		arrest2ref.clear();
		arrest2ref2bc.clear();
	}

}
