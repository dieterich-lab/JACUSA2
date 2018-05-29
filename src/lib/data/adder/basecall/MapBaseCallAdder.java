package lib.data.adder.basecall;

import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.basecall.map.MapBaseCallCount;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.count.BaseCallCount;

public class MapBaseCallAdder<T extends AbstractData>
extends AbstractDataAdder<T> 
implements BaseCallAdder<T> {

	private final BaseCallCountExtractor<T> baseCallCountExtractor;
	
	private final Map<Integer, Integer> winPos2coverage;
	private final Map<Integer, BaseCallCount> winPos2baseCallCount;

	public MapBaseCallAdder(
			final BaseCallCountExtractor<T> baseCallCountExtractor,
			final CoordinateController coordinateController) {

		super(coordinateController);

		this.baseCallCountExtractor = baseCallCountExtractor;

		final int n 			= coordinateController.getActiveWindowSize();
		winPos2coverage 		= new HashMap<Integer, Integer>(n);
		winPos2baseCallCount 	= new HashMap<Integer, BaseCallCount>(n);
	}
	
	@Override
	public void addData(T data, Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage(windowPosition) == 0) {
			return;
		}

		add(windowPosition, coordinate.getStrand(), baseCallCountExtractor.getBaseCallCount(data));
	}
	
	protected void add(final int windowPosition, final STRAND strand, final BaseCallCount dest) {
		dest.add(winPos2baseCallCount.get(windowPosition));
		if (strand == STRAND.REVERSE) {
			dest.invert();
		}		
	}

	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		int count = 0;
		if (! winPos2coverage.containsKey(windowPosition)) {
			winPos2baseCallCount.put(windowPosition, new MapBaseCallCount());
		} else {
			count = winPos2coverage.get(windowPosition);
		}
		winPos2coverage.put(windowPosition, count + 1);
		winPos2baseCallCount.get(windowPosition).increment(base);
	}

	@Override
	public void clear() {
		winPos2coverage.clear();
		winPos2baseCallCount.clear();
	}

	@Override
	public int getCoverage(final int windowPosition) {
		return winPos2coverage.containsKey(windowPosition) ? winPos2coverage.get(windowPosition) : 0;
	}
	
	@Override
	public BaseCallCount getBaseCallCount(final int windowPosition) {
		return winPos2baseCallCount.get(windowPosition);
	}
	
}
