package jacusa.filter.factory;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;

import jacusa.filter.Filter;
import jacusa.filter.HomozygousFilter;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments
 * @param 
 */

public class HomozygousFilterFactory
extends AbstractFilterFactory {

	// which condition is required to be homozygous
	private int homozygousConditionIndex;
	private final int conditionSize;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public HomozygousFilterFactory(
			final int conditionSize, final Fetcher<BaseCallCount> bccFetcher) {
		
		super(getOptionBuilder().build());
				
		homozygousConditionIndex 	= -1;
		this.conditionSize 			= conditionSize;
		this.bccFetcher 			= bccFetcher;
	}

	@Override
	public Options getOptions() {
		final Options options = new Options();
		options.addOption(getConditionOptionBuilder().build());
		return options;
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) throws IllegalArgumentException {
		final Set<Option> parsed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "condition":
				final int conditionIndex = Integer.parseInt(cmd.getOptionValue(longOpt));
				// make sure conditionIndex is within provided conditions
				if (conditionIndex >= 1 && conditionIndex <= conditionSize) {
					this.homozygousConditionIndex = conditionIndex - 1;
				} else {
					throw new IllegalArgumentException("Invalid argument: " + longOpt);
				}
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}

	@Override
	public void initDataTypeContainer(AbstractBuilder builder) {
		// not needed
	}
	
	@Override
	protected Filter createFilter(
			CoordinateController coordinateController, 
			ConditionContainer conditionContainer) {
		return new HomozygousFilter(getC(), homozygousConditionIndex, bccFetcher);
	}

	@Override
	public RecordWrapperProcessor createFilterCache(
			ConditionParameter conditionParameter,
			SharedCache sharedCache) {
		return null;
	}
		
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('H'))
				.desc("Filter non-homozygous sites in condition 1 or 2.");
	}
			 
	public static Builder getConditionOptionBuilder() {
		return Option.builder()
			.longOpt("condition")
			.argName("CONDITION")
			.hasArg()
			.required()
			.desc("Possible values for condition: 1 or 2.");
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer data) {
		sb.append(Util.EMPTY_FIELD);	
	}

	public int getHomozygousConditionIndex() {
		return homozygousConditionIndex;
	}
	
}
