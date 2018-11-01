package jacusa.filter.factory;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import htsjdk.samtools.util.StringUtil;

import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.HomopolymerFilter;
import jacusa.filter.cache.HomopolymerRecordFilterCache;
import jacusa.filter.cache.HomopolymerReferenceFilterCache;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.FilteredDataFetcher;
import lib.data.cache.fetcher.SpecificFilteredDataFetcher;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;
import lib.util.coordinate.CoordinateController;

public class HomopolymerFilterFactory 
extends AbstractFilterFactory {

	// default length of consecutive identical base call for
	// a homopolymer
	private static final int MIN_HOMOPOLYMER_LENGTH = 7;
	private static final HomopolymerMethod HOMOPOLYMER_METHOD = HomopolymerMethod.REFERENCE;

	// chosen length of homopolymer
	private int length;
	private HomopolymerMethod homopolymerMethod;
	
	private final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredBooleanFetcher;
	private final DataType<BooleanWrapperFilteredData> dataType;
	
	public HomopolymerFilterFactory(final FilteredDataFetcher<BooleanWrapperFilteredData, BooleanWrapper> filteredDataFetcher) {
		super(getOptionBuilder().build());
		length = MIN_HOMOPOLYMER_LENGTH;
		homopolymerMethod = HOMOPOLYMER_METHOD;
		
		this.filteredBooleanFetcher = filteredDataFetcher;
		dataType = filteredDataFetcher.getDataType();
	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('Y'))
				.desc("Filter wrong variant calls within homopolymers.");
	}
	
	public static Builder getHomopolymerOptionBuilder() {
		return Option.builder()
				.longOpt("length")
				.argName("LENGTH")
				.hasArg()
				.desc("must be > 0. Default: " + MIN_HOMOPOLYMER_LENGTH);
	}
	
	public static Builder getHomopolymerMethodBuilder() {
		final String s = StringUtil.join(", ", HomopolymerMethod.values());
		final StringBuilder sb = new StringBuilder();
		sb.append("Choose how to compute homopolymers: ");
		sb.append(s);
		sb.append(". Default: " + HOMOPOLYMER_METHOD);
		return Option.builder()
				.longOpt("method")
				.argName("METHOD")
				.hasArg()
				.desc(sb.toString());
	}
	
	@Override
	public Options getOptions() {
		final Options options = new Options();
		options.addOption(getHomopolymerOptionBuilder().build());
		return options;
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) {
		final Set<Option> parsed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "length":
				final int length = Integer.valueOf(cmd.getOptionValue(longOpt));
				if (length <= 0) {
					throw new IllegalArgumentException("Invalid length: " + longOpt);
				}
				this.length = length;
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}

	@Override
	public RecordWrapperDataCache createFilterCache(
			AbstractConditionParameter conditionParameter,
			SharedCache sharedCache) {
		
		switch (homopolymerMethod) {
		case REFERENCE:
			return new HomopolymerReferenceFilterCache(
					getC(), 
					filteredBooleanFetcher, 
					length, 
					sharedCache);

		case READ:
			return new HomopolymerRecordFilterCache(
					getC(), 
					filteredBooleanFetcher, 
					length, 
					sharedCache);
			
		default:
			throw new IllegalArgumentException("Unknown Homopolymer method: " + homopolymerMethod.toString());
		}
	}
	
	@Override
	public void inidDataTypeContainer(AbstractBuilder builder) {
		if (! builder.contains(dataType)) { 
			builder.with(dataType);
		}
	}
	
	@Override
	public AbstractFilter createFilter(final CoordinateController coordinateController,
			final ConditionContainer conditionContainer) {
 
		return new HomopolymerFilter(
				getC(), 
				length, 
				new SpecificFilteredDataFetcher<>(getC(), filteredBooleanFetcher));
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer container) {
		if (filteredBooleanFetcher.fetch(container).contains(getC())) {
			sb.append('1');
		} else {
			sb.append('0');
		}
	}
	
	public int getLength() {
		return length;
	}

	private enum HomopolymerMethod {
		REFERENCE, READ;
	}
	
}
