package lib.data.adder.basecall;

import java.util.Arrays;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.SequenceUtil;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class ArrayBaseCallAdder
extends AbstractDataContainerAdder 
implements IncrementAdder {

	private final int[] coverage;
	private final int[][] baseCall;

	private final Fetcher<BaseCallCount> bccFetcher;
	
	public ArrayBaseCallAdder(
			final Fetcher<BaseCallCount> bccFetcher,
			final SharedCache sharedCache) {

		super(sharedCache);

		this.bccFetcher = bccFetcher;

		coverage = new int[getCoordinateController().getActiveWindowSize()];
		baseCall = new int[getCoordinateController().getActiveWindowSize()][SequenceUtil.VALID_BASES_UPPER.length];
	}
	
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		if (bccFetcher == null) {
			return;
		}
		
		final int windowPosition = getCoordinateController().getCoordinateTranslator().convert2windowPosition(coordinate);
		if (getCoverage(windowPosition) == 0) {
			return;
		}

		final BaseCallCount dest = bccFetcher.fetch(container);
		add(windowPosition, coordinate.getStrand(), dest);
	}
	
	public void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		for (final Base base : Base.validValues()) {
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

}
