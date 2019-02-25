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
import lib.data.filter.BooleanFilteredData;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.data.filter.BooleanData;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This FilterFactory configures and creates the Homopolymer Filter. The user can chosen the minLength
 * that is needed for a consecutive sequence of identical base calls to be called a homopolymer.
 * In the future the user will be able to decide how to define a homopolyer: reference or read based.
 * While both methods are implemented, currently only the read based homopolymer definition is available.
 * Reference based homopolyer calling needs more testing and optimization.
 */
public class HomopolymerFilterFactory
extends AbstractFilterFactory 
implements HasHomopolymerLength, HasHomopolymerMethod {

	public static final char FILTER = 'Y';
	
	// default length of consecutive identical base call for a homopolymer
	public static final int MIN_HOMOPOLYMER_LENGTH 				= 7;
	// default method to define a homopolymer
	public static final HomopolymerMethod HOMOPOLYMER_METHOD 	= HomopolymerMethod.READ;

	// chosen length of homopolymer
	private int length;
	private HomopolymerMethod method;
	
	private final GeneralParameter parameter;
	// define the location where homopolymer indicator will be stored within a dataContainer
	private final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredBooleanFetcher;
	private final DataType<BooleanFilteredData> dataType;

	public HomopolymerFilterFactory(
			final GeneralParameter parameter,
			final FilteredDataFetcher<BooleanFilteredData, BooleanData> filteredDataFetcher) {

		super(getOptionBuilder().build());
		length = MIN_HOMOPOLYMER_LENGTH;
		method = HOMOPOLYMER_METHOD;
		
		getACOption().add(new HomopolymerLengthOption(this));
		// current turned off - needs more optimization 
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
			// since the reference will be identical for all BAMs and conditions within a 
			// thread window make sure that homopolymers get called only once in the reference and
			// the result gets shared with all the other instances - needs optimization
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
