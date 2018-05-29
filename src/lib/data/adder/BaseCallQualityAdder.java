package lib.data.adder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.SAMRecord;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.basecall.map.MapBaseCallQualitityCount;
import lib.data.count.BaseCallQualityCount;
import lib.data.has.HasPileupCount;

public class BaseCallQualityAdder<T extends AbstractData & HasPileupCount>
extends AbstractDataAdder<T> 
implements IncrementAdder<T> {

	private Map<Integer, BaseCallQualityCount> baseCallQualities;
	
	public BaseCallQualityAdder(final CoordinateController coordinateController) {
		super(coordinateController);
		final int n  = coordinateController.getActiveWindowSize();
		baseCallQualities = new HashMap<Integer, BaseCallQualityCount>(n / 2);
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (! baseCallQualities.containsKey(windowPosition)) {
			return;
		}

		final Set<Base> alleles = baseCallQualities.get(windowPosition).getAlleles();
		data.getPileupCount().getBaseCallQualityCount().add(alleles, baseCallQualities.get(windowPosition));
	}
	
	@Override
	public void increment(final int referencePosition, final int windowPosition, final int readPosition,
			final Base base, final byte baseQual,
			final SAMRecord record) {

		if (! baseCallQualities.containsKey(windowPosition)) {
			baseCallQualities.put(windowPosition, new MapBaseCallQualitityCount());
		}
		final BaseCallQualityCount base2qual2count = baseCallQualities.get(windowPosition);
		base2qual2count.increment(base, baseQual);
	}
	
	@Override
	public void clear() {
		baseCallQualities.clear();
	}

}
