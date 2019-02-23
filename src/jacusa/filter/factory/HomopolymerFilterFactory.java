package jacusa.filter.factory;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.Filter;
import jacusa.filter.HomopolymerFilter;
import jacusa.filter.homopolymer.HomopolymerStorage;
import jacusa.filter.homopolymer.HomopolymerReadRecordProcessor;
import jacusa.filter.homopolymer.HomopolymerReferenceRecordProcessor;
import jacusa.filter.homopolymer.HomopolymerReferenceStorage;
import lib.cli.options.filter.HomopolymerLengthOption;
import lib.cli.options.filter.has.HasHomopolymerLength;
import lib.cli.options.filter.has.HasHomopolymerMethod;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.SpecificFilteredDataFetcher;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.data.filter.BooleanWrapper;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

public class HomopolymerFilterFactory
extends AbstractFilterFactory 
implements HasHomopolymerLength, HasHomopolymerMethod {

	public static final char FILTER = 'Y';
	
	// default length of consecutive identical base call for
	// a homopolymer
	public static final int MIN_HOMOPOLYMER_LENGTH = 7;
	public static final HomopolymerMethod HOMOPOLYMER_METHOD = HomopolymerMethod.READ;

	// chosen length of homopolymer
	private int length;
	private HomopolymerMethod method;
	
	private final GeneralParameter parameter;
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredBooleanFetcher;
	private final DataType<BooleanWrapperFilteredData> dataType;

	public HomopolymerFilterFactory(
			final GeneralParameter parameter,
			final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher) {

		super(getOptionBuilder().build());
		length = MIN_HOMOPOLYMER_LENGTH;
		method = HOMOPOLYMER_METHOD;
		
		getACOption().add(new HomopolymerLengthOption(this));
		// getACOption().add(new HomopolymerMethodOption(this));

		this.parameter 				= parameter;
		this.filteredBooleanFetcher = filteredDataFetcher;
		dataType 					= filteredDataFetcher.getDataType();
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(FILTER))
				.desc("Filter wrong variant calls within homopolymers.");
	}

	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {
		
		final Cache cache = new Cache();
		
		switch (method) {
		case REFERENCE:
			final int bamFileCount = parameter.getBAMfileCount();
			final HomopolymerReferenceStorage refStorage = new HomopolymerReferenceStorage(
					sharedStorage,
					getC(), filteredBooleanFetcher, 
					length,
					bamFileCount);
			cache.addStorage(refStorage);

			final HomopolymerReferenceRecordProcessor refRecordProcessor = 
					new HomopolymerReferenceRecordProcessor(length, refStorage);
			cache.addRecordProcessor(refRecordProcessor);
			
			break;

		case READ:
			final HomopolymerStorage readStorage = new HomopolymerStorage(
					sharedStorage, getC(), filteredBooleanFetcher, length);
			cache.addStorage(readStorage);
			
			final HomopolymerReadRecordProcessor readRecordProcessor = 
					new HomopolymerReadRecordProcessor(length, readStorage);
			cache.addRecordProcessor(readRecordProcessor);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown Homopolymer method: " + method.toString());
		}
		
		return cache;
	}
	
	@Override
	public void initDataContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
	}
	
	@Override
	public Filter createFilter(final CoordinateController coordinateController,
			final ConditionContainer conditionContainer) {
 
		return new HomopolymerFilter(
				getC(), 
				length, 
				new SpecificFilteredDataFetcher<>(getC(), filteredBooleanFetcher));
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer container) {
		if (filteredBooleanFetcher.fetch(container).contains(getC())) {
			sb.append('1');
		} else {
			sb.append('0');
		}
	}

	@Override
	public void setHomopolymerMethod(HomopolymerMethod method) {
		this.method = method;
	}
	
	@Override
	public void setHomopolymerLength(int length) {
		this.length = length;
	}
	
	@Override
	public HomopolymerMethod getHomopolymerMethod() {
		return method;
	}
	
	@Override
	public int getHomopolymerLength() {
		return length;
	}
	
}
