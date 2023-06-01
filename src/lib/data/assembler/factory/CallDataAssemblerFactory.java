package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.call.CallMethod.CallBuilderFactory;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.basecall.ArrayBCQStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.AlignmentBlockProcessor;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MaxDepthValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.util.coordinate.CoordinateTranslator;

public class CallDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	private final Fetcher<PileupCount> pcFetcher;
	
	public CallDataAssemblerFactory(final CallBuilderFactory builderFactory) {
		super(builderFactory);
		pcFetcher = DataType.PILEUP_COUNT.getFetcher();
	}

	@Override
	protected Cache createCache(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter) {

		final Cache cache = new Cache();
		
		final ArrayBCQStorage bcqcStorage = new ArrayBCQStorage(
				sharedStorage, pcFetcher);
		cache.addStorage(bcqcStorage);
		
		final List<Validator> validators = new ArrayList<Validator>();
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}
		if (conditionParameter.getMaxDepth() > 0) {
			validators.add(new MaxDepthValidator(conditionParameter.getMaxDepth(), bcqcStorage));
		}

		final CoordinateTranslator translator = 
				sharedStorage.getCoordinateController().getCoordinateTranslator();
		
		final PositionProcessor positionProcessor = new PositionProcessor(validators, bcqcStorage);
		cache.addRecordProcessor(new AlignmentBlockProcessor(translator, positionProcessor));

		if (parameter.showInsertionCount() || parameter.showInsertionStartCount() || parameter.showDeletionCount()) {
			cache.addCache(createCoverageCache(sharedStorage, DataType.COVERAGE.getFetcher()));
		}
		addDeletionCache(parameter, sharedStorage, cache);
		addInsertionCache(parameter, sharedStorage, cache);

		stratifyByBaseSub(parameter, sharedStorage,  conditionParameter, cache);

		return cache;
	}
	
}
