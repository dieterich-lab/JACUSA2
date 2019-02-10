package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.JACUSA;
import jacusa.filter.Filter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.GenericBaseCallCountFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.homopolymer.CollectionRecordExtendedProcessors;
import lib.cli.options.filter.FilterDistanceOption;
import lib.cli.options.filter.FilterMinRatioOption;
import lib.cli.options.filter.has.HasFilterDistance;
import lib.cli.options.filter.has.HasFilterMinRatio;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataType;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.SpecificFilteredDataFetcher;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.ArrayBaseCallStorage;
import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.data.stroage.Storage;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MaxDepthValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.UniqueVisitReadPositionValidator;
import lib.data.validator.Validator;
import lib.io.InputOutput;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments
 */
public abstract class AbstractBaseCallCountFilterFactory
extends AbstractFilterFactory 
implements HasFilterDistance, HasFilterMinRatio {

	public static final int DEFAULT_FILTER_DISTANCE 	= 6;
	public static final double DEFAULT_FILTER_MINRATIO 	= 0.5;
	
	private Fetcher<BaseCallCount> observedBccFetcher;
	private final Fetcher<BaseCallCount> filteredBccFetcher;
	private final DataType<BaseCallCountFilteredData> dataType;
	
	private int filterDistance;
	private double filterMinRatio;

	private final BaseCallCount.AbstractParser baseCallCountParser;
	
	public AbstractBaseCallCountFilterFactory(
			final Option option,
			final Fetcher<BaseCallCount> observedBccFetcher, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		this(
				option, 
				observedBccFetcher, filteredDataFetcher, 
				DEFAULT_FILTER_DISTANCE, DEFAULT_FILTER_MINRATIO);
	}
	
	public AbstractBaseCallCountFilterFactory(
			final Option option,
			final Fetcher<BaseCallCount> observedBccFetcher, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option);
		
		this.observedBccFetcher = observedBccFetcher;
		filteredBccFetcher 		= new SpecificFilteredDataFetcher<>(getC(), filteredDataFetcher);
		dataType 				= filteredDataFetcher.getDataType();
		
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;

		getACOption().add(new FilterDistanceOption(this));
		getACOption().add(new FilterMinRatioOption(this));
		
		baseCallCountParser = new DefaultBaseCallCount.Parser(
				InputOutput.EMPTY_FIELD, InputOutput.VALUE_SEP);
	}
	
	@Override
	public void initDataContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
		final BaseCallCountFilteredData filteredData = builder.get(dataType);
		if (! filteredData.contains(getC())) {
			filteredData.add(getC(), JACUSA.BCC_FACTORY.create());
		}
	}
	
	@Override
	public Filter createFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {
		
		return new GenericBaseCallCountFilter(getC(),
				observedBccFetcher,
				filteredBccFetcher,	
				filterDistance, new FilterByRatio(getFilterMinRatio()));
	}
	
	@Override
	public Cache createFilterCache(
			final ConditionParameter conditionParameter,
			final SharedStorage sharedStorage) {

		final Cache cache = new Cache();
		final List<Storage> storages = new ArrayList<>();
		
		final VisitedReadPositionStorage visitedStorage = new VisitedReadPositionStorage(sharedStorage);
		storages.add(visitedStorage);
		
		final AbstractBaseCallCountStorage bccStorage = 
				new ArrayBaseCallStorage(sharedStorage, filteredBccFetcher);
		storages.add(bccStorage);
		
		final List<Validator> validators = new ArrayList<Validator>();
		UniqueVisitReadPositionValidator uniqueValidator = 
				new UniqueVisitReadPositionValidator(visitedStorage);
		validators.add(uniqueValidator);
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMaxDepth() > 0) {
			validators.add(new MaxDepthValidator(conditionParameter.getMaxDepth(), bccStorage));
		}
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}
		
		final PositionProcessor positionProcessor = new PositionProcessor(
				validators, storages);
		
		cache.addRecordProcessor(new CollectionRecordExtendedProcessors(
				visitedStorage,
				createRecordProcessors(sharedStorage, positionProcessor)));
		cache.addStorages(storages);
		
		return cache;
	}
	
	@Override
	public int getFilterDistance() {
		return filterDistance;
	}

	@Override
	public void setFilterDistance(int distance) {
		this.filterDistance = distance;
	}
	
	@Override
	public double getFilterMinRatio() {
		return filterMinRatio;
	}
	
	@Override
	public void setFilterMinRatio(double minRatio) {
		this.filterMinRatio = minRatio;
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer filteredData) {
		baseCallCountParser.wrap(filteredBccFetcher.fetch(filteredData));
	}
	
	protected abstract List<RecordExtendedProcessor> createRecordProcessors(
			SharedStorage sharedStorage, final PositionProcessor positionProcessor);
	
}
