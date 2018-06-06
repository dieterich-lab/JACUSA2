package lib.data.adder.basecall;

import java.util.Arrays;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.SequenceUtil;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.count.BaseCallCount;

public class ArrayBaseCallAdder<T extends AbstractData>
extends AbstractDataAdder<T> 
implements BaseCallAdder<T> {

	private final BaseCallCountExtractor<T> baseCallCountExtractor;

	private final int[] coverage;
	private final int[][] baseCall;

	public ArrayBaseCallAdder(
			final BaseCallCountExtractor<T> baseCallCountExtractor,
			final CoordinateController coordinateController) {

		super(coordinateController);

		this.baseCallCountExtractor = baseCallCountExtractor;

		coverage = new int[coordinateController.getActiveWindowSize()];
		baseCall = new int[coordinateController.getActiveWindowSize()][SequenceUtil.VALID_BASES_UPPER.length];
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		if (baseCallCountExtractor == null) {
			return;
		}
		
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage(windowPosition) == 0) {
			return;
		}

		add(windowPosition, coordinate.getStrand(), baseCallCountExtractor.getBaseCallCount(data));
	}
	
	public void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		for (Base base : Base.validValues()) {
			if (baseCall[windowPosition][base.getIndex()] > 0) {
				dest.set(base, baseCall[windowPosition][base.getIndex()]);
			}
		}
		
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}
	}

	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		coverage[windowPosition] 					+= 1;
		baseCall[windowPosition][base.getIndex()] 	+= 1;
	}

	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		for (int[] b : baseCall) {
			Arrays.fill(b, 0);	
		}
	}

	@Override
	public int getCoverage(final int windowPosition) {
		return coverage[windowPosition];
	}
	
	@Override
	public BaseCallCount getBaseCallCount(final int windowPosition) {
		return new ArrayBaseCallCount(baseCall[windowPosition]);
	}

}
