package jacusa.filter.factory.basecall;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.Filter;
import jacusa.filter.FilterByRatio;
import jacusa.filter.GenericBaseCallCountFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.processrecord.UniquePositionRecordProcessors;
import lib.cli.options.filter.FilterDistanceOption;
import lib.cli.options.filter.FilterMinRatioOption;
import lib.cli.options.filter.has.HasFilterDistance;
import lib.cli.options.filter.has.HasFilterMinRatio;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataType;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractDataContainerBuilder;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.data.fetcher.ExtractFilter;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.storage.Cache;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.ArrayBaseCallStorage;
import lib.data.storage.basecall.VisitedReadPositionStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordProcessor;
import lib.data.validator.DefaultBaseCallValidator;
import lib.data.validator.MaxDepthValidator;
import lib.data.validator.MinBASQValidator;
import lib.data.validator.UniqueVisitReadPositionValidator;
import lib.data.validator.Validator;
import lib.io.InputOutput;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This abstract FilterFactory configures and helps to create filters that need
 * an observed and filtered base call count, e.g.: Read/Start filter, etc.
 */
public abstract class AbstractBaseCallCountFilterFactory extends AbstractFilterFactory
		implements HasFilterDistance, HasFilterMinRatio {

	public static final int DEFAULT_FILTER_DISTANCE = 6;
	public static final double DEFAULT_FILTER_MINRATIO = 0.5;

	// defines where the observed base call count is extracted from a dataContainer
	private final DataType<BaseCallCount> observedDataType;
	// defines where the filtered base call count is extracted from a dataContainer
	private final DataType<FilteredBaseCallCount> filteredDataType;
	private final DataType<BaseCallCount> filteredDataTypeTODO;

	private int filterDistance;
	private double filterMinRatio;

	private final BaseCallCount.AbstractParser bccParser;

	public AbstractBaseCallCountFilterFactory(final Option option, final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType) {

		this(option, observedDataType, filteredDataType, DEFAULT_FILTER_DISTANCE, DEFAULT_FILTER_MINRATIO);
	}

	public AbstractBaseCallCountFilterFactory(final Option option, final DataType<BaseCallCount> observedDataType,
			final DataType<FilteredBaseCallCount> filteredDataType, final int defaultFilterDistance,
			final double defaultFilterMinRatio) {

		super(option);

		this.observedDataType = observedDataType;
		this.filteredDataType = filteredDataType;
		this.filteredDataTypeTODO = DataType.create("filter_" + getID(), BaseCallCount.class,
				new ExtractFilter<FilteredBaseCallCount, BaseCallCount>(getID(), filteredDataType));

		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;

		// CLI options
		getACOption().add(new FilterDistanceOption(this));
		getACOption().add(new FilterMinRatioOption(this));

		bccParser = new DefaultBCC.Parser(InputOutput.EMPTY_FIELD, InputOutput.VALUE_SEP);
	}

	@Override
	public void initDataContainer(AbstractDataContainerBuilder builder) {
		// create a container for base call count filtered data
		if (!builder.contains(filteredDataType)) {
			builder.with(filteredDataType);
		}

		final FilteredBaseCallCount filteredData = builder.get(filteredDataType);
		// make sure that in base call count filtered data there is a bcc for this
		// filter
		if (!filteredData.contains(getID())) {
			filteredData.add(getID(), BaseCallCount.create());
		}
	}

	@Override
	public Filter createFilter(final CoordinateController coordinateController,
			final ConditionContainer conditionContainer) {

		// create an instance of a base call count filter
		// use fetchers to locate observed and filtered counts
		return new GenericBaseCallCountFilter(getID(), observedDataType, filteredDataTypeTODO, filterDistance,
				new FilterByRatio(getFilterMinRatio()));
	}

	@Override
	public Cache createFilterCache(final ConditionParameter conditionParameter, final SharedStorage sharedStorage) {
		final List<Storage> storages = new ArrayList<>();

		// make sure each read positions get used only once
		final VisitedReadPositionStorage visitedStorage = new VisitedReadPositionStorage(sharedStorage);
		storages.add(visitedStorage);

		// TODO filteredDataType -> extract BaseCallCount getID()
		final AbstractBaseCallCountStorage bccStorage = new ArrayBaseCallStorage(sharedStorage, observedDataType);
		storages.add(bccStorage);
		// add validators
		final List<Validator> validators = new ArrayList<>();
		UniqueVisitReadPositionValidator uniqueValidator = new UniqueVisitReadPositionValidator(visitedStorage);
		validators.add(uniqueValidator);
		validators.add(new DefaultBaseCallValidator());
		if (conditionParameter.getMaxDepth() > 0) {
			validators.add(new MaxDepthValidator(conditionParameter.getMaxDepth(), bccStorage));
		}
		if (conditionParameter.getMinBASQ() > 0) {
			validators.add(new MinBASQValidator(conditionParameter.getMinBASQ()));
		}

		final PositionProcessor positionProcessor = new PositionProcessor(validators, storages);
		final Cache cache = new Cache();
		cache.addRecordProcessor(new UniquePositionRecordProcessors(visitedStorage,
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
		bccParser.wrap(filteredData.get(filteredDataType).get(getID()));
	}

	/**
	 * Returs a list of custom record processors.
	 * 
	 * @param sharedStorage
	 * @param positionProcessor
	 * @return list of record processors
	 */
	protected abstract List<RecordProcessor> createRecordProcessors(SharedStorage sharedStorage,
			final PositionProcessor positionProcessor);

}
