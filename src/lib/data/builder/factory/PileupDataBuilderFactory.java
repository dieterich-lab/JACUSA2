package lib.data.builder.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.PileupData;
import lib.data.cache.PileupDataCache;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.record.AlignmentBlockWrapperDataCache;
import lib.data.cache.record.RecordDataCache;
import lib.util.coordinate.CoordinateController;

public class PileupDataBuilderFactory 
extends AbstractDataBuilderFactory<PileupData> {

	public PileupDataBuilderFactory(final AbstractParameter<PileupData, ?> generalParameter) {
		super(generalParameter);
	}
	
	protected List<RecordDataCache<PileupData>> createDataCaches(final CoordinateController coordinateController, 
			final AbstractConditionParameter<PileupData> conditionParameter) {

		final List<RecordDataCache<PileupData>> caches = new ArrayList<RecordDataCache<PileupData>>(1);
		caches.add(
				new AlignmentBlockWrapperDataCache<PileupData>(
					new PileupDataCache<PileupData>(
							new DefaultBaseCallCountExtractor<PileupData>(),
							conditionParameter.getMaxDepth(), 
							conditionParameter.getMinBASQ(), 
							getParameter().getBaseConfig(), coordinateController)));
		return caches;
	}
	
}
