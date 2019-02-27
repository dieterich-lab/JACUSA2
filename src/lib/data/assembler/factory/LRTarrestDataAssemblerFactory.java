package lib.data.assembler.factory;

import java.util.ArrayList;
import java.util.List;

import jacusa.method.lrtarrest.LRTarrestMethod.LRTarrestBuilderFactory;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.basecall.MapBaseCallQualityStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.storage.lrtarrest.ArrestPositionCalculator;
import lib.data.storage.lrtarrest.EndArrestPosition;
import lib.data.storage.lrtarrest.LRTarrestBaseCallStorage;
import lib.data.storage.lrtarrest.StartArrestPosition;
import lib.data.storage.processor.AlignmentBlockProcessor;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.Validator;
import lib.util.LibraryType;

public class LRTarrestDataAssemblerFactory 
extends AbstractSiteDataAssemblerFactory {

	public LRTarrestDataAssemblerFactory(final LRTarrestBuilderFactory builderFactory) {
		super(builderFactory);
	}

	@Override
	public Cache createCache(
			final GeneralParameter parameter,
			final SharedStorage sharedStorage, 
			final ConditionParameter conditionParameter) {
		
		final LibraryType libraryType = conditionParameter.getLibraryType();
		
		final Fetcher<ArrestPosition2baseCallCount> ap2bccFetcher =
				DataType.AP2BCC.getFetcher();

		ArrestPositionCalculator apc = null;
		
		switch (libraryType) {

		case RF_FIRSTSTRAND:
			apc = new EndArrestPosition();
			break;

		case FR_SECONDSTRAND:
			apc = new StartArrestPosition();
			break;
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		final List<Storage> storages = new ArrayList<Storage>(2);
		final Fetcher<PileupCount> pileupFetcher = DataType.PILEUP_COUNT.getFetcher();
		final Storage pileupStorage = new MapBaseCallQualityStorage(sharedStorage, pileupFetcher);
		final Storage arrestPosStorage = new LRTarrestBaseCallStorage(sharedStorage, apc, ap2bccFetcher);
		storages.add(arrestPosStorage);
		storages.add(pileupStorage);
		
		final List<Validator> validators = new ArrayList<Validator>();
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}

		final PositionProcessor positionProcessor = new PositionProcessor(validators, storages);

		final Cache cache = new Cache();
		cache.addRecordProcessor(new AlignmentBlockProcessor(
				sharedStorage.getCoordinateController().getCoordinateTranslator(), 
				positionProcessor));
		cache.addStorages(storages);
		return cache;
	}

}
