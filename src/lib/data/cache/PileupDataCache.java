package lib.data.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.basecall.map.MapBaseCallQualitityCount;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.region.ArrayBaseCallRegionDataCache;
import lib.data.count.BaseCallQualityCount;
import lib.data.has.HasPileupCount;

public class PileupDataCache<T extends AbstractData & HasPileupCount>
extends ArrayBaseCallRegionDataCache<T> {

	private Map<Integer, BaseCallQualityCount> baseCallQualities;
	
	public PileupDataCache(final BaseCallCountExtractor<T> baseCallCountExtractor, 
			final int maxDepth, final byte minBASQ, 
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(baseCallCountExtractor, maxDepth, minBASQ, baseCallConfig, coordinateController);

		final int n  = coordinateController.getActiveWindowSize();
		baseCallQualities = new HashMap<Integer, BaseCallQualityCount>(n / 2);
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage()[windowPosition] == 0) {
			return;
		}

		final Set<Integer> alleles = baseCallQualities.get(windowPosition).getAlleles();
		data.getPileupCount().getBaseCallQualityCount().add(alleles, baseCallQualities.get(windowPosition));
		
		super.addData(data, coordinate);
	}
	
	@Override
	public void increment(final int windowPosition, final int readPosition,
			final int baseIndex, final byte baseQual) {

		super.increment(windowPosition, readPosition, baseIndex, baseQual);
		if (! baseCallQualities.containsKey(windowPosition)) {
			baseCallQualities.put(windowPosition, new MapBaseCallQualitityCount());
		}
		final BaseCallQualityCount base2qual2count = baseCallQualities.get(windowPosition);
		base2qual2count.increment(baseIndex, baseQual);
	}
	
	@Override
	public void clear() {
		super.clear();
		baseCallQualities.clear();
	}
	
}
