package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.basecall.MapBCQStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.AlignmentBlockProcessor;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MaxDepthValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;

public class PileupDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	private final Fetcher<PileupCount> pcFetcher;
	
	public PileupDataAssemblerFactory(final AbstractBuilderFactory builderFactory) {
		super(builderFactory);
		pcFetcher = DataType.PILEUP_COUNT.getFetcher();
	}
	
	@Override
	protected Cache createCache(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter) {

		final MapBCQStorage bcqcStorage = new MapBCQStorage(sharedStorage, pcFetcher);

		final List<Validator> validators = new ArrayList<>();
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}
		if (conditionParameter.getMaxDepth() > 0) {
			validators.add(new MaxDepthValidator(conditionParameter.getMaxDepth(), bcqcStorage));
		}

		final PositionProcessor positionProcessor = new PositionProcessor(validators, bcqcStorage);
		
		final Cache cache = new Cache();
		cache.addRecordProcessor(new AlignmentBlockProcessor(
				sharedStorage.getCoordinateController().getCoordinateTranslator(),
				positionProcessor));
		cache.addStorage(bcqcStorage);
		
		addDeletionCache(parameter, sharedStorage, cache);
		stratifyByBaseSub(parameter, sharedStorage,  conditionParameter, cache);
		
		return cache;
	}
	
}
