package jacusa.filter.factory.basecall.lrtarrest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.Option;

import jacusa.filter.Filter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.GenericBaseCallCountFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.filter.homopolymer.CollectionRecordExtendedProcessors;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.options.filter.FilterDistanceOption;
import lib.cli.options.filter.FilterMinRatioOption;
import lib.cli.options.filter.has.HasApply2reads;
import lib.cli.options.filter.has.HasFilterDistance;
import lib.cli.options.filter.has.HasFilterMinRatio;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataType;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Apply2ReadsArrestPos2BaseCallCountSwitch;
import lib.data.fetcher.Fetcher;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.SpecificFilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.storage.lrtarrest.ArrestPositionCalculator;
import lib.data.storage.lrtarrest.EndArrestPosition;
import lib.data.storage.lrtarrest.LRTarrestBaseCallStorage;
import lib.data.storage.lrtarrest.StartArrestPosition;
import lib.data.storage.processor.RecordExtendedProcessor;
import lib.data.stroage.Storage;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.UniqueVisitReadPositionValidator;
import lib.data.validator.Validator;
import lib.util.ConditionContainer;
import lib.util.LibraryType;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractLRTarrestBaseCallCountFilterFactory 
extends AbstractFilterFactory 
implements HasFilterDistance, HasFilterMinRatio, HasApply2reads {

	private final Apply2readsBaseCallCountSwitch bccSwitch;
	private final Fetcher<ArrestPosition2baseCallCount> filteredAp2bccFetcher;
	private final Fetcher<BaseCallCount> filteredBccExtractor;
	private final DataType<ArrestPos2BaseCallCountFilteredData> dataType;
	
	private int filterDistance;
	private double filterMinRatio;

	public AbstractLRTarrestBaseCallCountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<ArrestPos2BaseCallCountFilteredData, ArrestPosition2baseCallCount> filteredDataFetcher) {
		
		this(
				option, 
				bccSwitch, filteredDataFetcher, 
				AbstractBaseCallCountFilterFactory.DEFAULT_FILTER_DISTANCE,
				AbstractBaseCallCountFilterFactory.DEFAULT_FILTER_MINRATIO);
	}
	
	public AbstractLRTarrestBaseCallCountFilterFactory(
			final Option option,
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<ArrestPos2BaseCallCountFilteredData, ArrestPosition2baseCallCount> filteredDataFetcher, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option);
		this.bccSwitch = bccSwitch;
		filteredAp2bccFetcher = 
				new SpecificFilteredDataFetcher<>(getC(), filteredDataFetcher);
		filteredBccExtractor = 
				new Apply2ReadsArrestPos2BaseCallCountSwitch(
						bccSwitch.getApply2reads(),
						filteredAp2bccFetcher);
		dataType = filteredDataFetcher.getDataType();
		
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;
		getACOption().add(new FilterDistanceOption(this));
		getACOption().add(new FilterMinRatioOption(this));
	}

	@Override
	public void initDataContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
		final ArrestPos2BaseCallCountFilteredData filteredData = builder.get(dataType);
		if (! filteredData.contains(getC())) {
			filteredData.add(getC(), new ArrestPosition2baseCallCount());
		}	
	}

	@Override
	public void addFilteredData(StringBuilder sb, DataContainer filteredData) {
		// FIXME implement - maybe change of interface needed
		sb.append("TODO");
	}
	
	@Override
	public Filter createFilter(CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new GenericBaseCallCountFilter(getC(),
			bccSwitch,
			filteredBccExtractor,	
			filterDistance, new FilterByRatio(filterMinRatio));
	}
	
	@Override
	public Cache createFilterCache(
			final ConditionParameter conditionParameter,
			final SharedStorage sharedStorage) {

		final LibraryType libraryType = conditionParameter.getLibraryType();

		final Fetcher<ArrestPosition2baseCallCount> ap2bccFetcher =
				DataType.AP2BCC.getFetcher();

		ArrestPositionCalculator arrestPositionCalc = null;
		
		switch (libraryType) {

		case RF_FIRSTSTRAND:
			arrestPositionCalc = new EndArrestPosition();
			break;

		case FR_SECONDSTRAND:
			arrestPositionCalc = new StartArrestPosition();
			break;
			
		default:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + libraryType.toString());
		}

		final List<Storage> storages = new ArrayList<Storage>();
		final VisitedReadPositionStorage visitedStorage = 
				new VisitedReadPositionStorage(sharedStorage);
		storages.add(visitedStorage);
		final Storage lrtArrestStorage = new LRTarrestBaseCallStorage(
				sharedStorage, arrestPositionCalc, ap2bccFetcher);
		storages.add(lrtArrestStorage);
		
		final List<Validator> validators = new ArrayList<Validator>();
		UniqueVisitReadPositionValidator uniqueValidator = 
				new UniqueVisitReadPositionValidator(visitedStorage);
		validators.add(uniqueValidator);
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}
		
		final PositionProcessor positionProcessor = 
				new PositionProcessor(validators, storages);

		final Cache cache = new Cache();
		cache.addRecordProcessor(new CollectionRecordExtendedProcessors(
				visitedStorage,
				createRecordProcessors(sharedStorage, positionProcessor)));
		cache.addStorages(storages);		
		
		return cache;
	}
	
	@Override
	public void setFilterMinRatio(double minRatio) {
		this.filterMinRatio = minRatio;
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
	public int getFilterDistance() {
		return filterDistance;
	}

	@Override
	public Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
	protected abstract List<RecordExtendedProcessor> createRecordProcessors(
			final SharedStorage sharedStorage,
			final PositionProcessor positionProcessor);
	
}