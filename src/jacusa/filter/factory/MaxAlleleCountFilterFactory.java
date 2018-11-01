package jacusa.filter.factory;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.MaxAlleleFilter;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
// <D extends AbstractData & HasBaseCallCount> 
public class MaxAlleleCountFilterFactory
extends AbstractFilterFactory {

	// default value for max alleles
	public static final int MAX_ALLELES = 2;

	// chosen value
	private int maxAlleles;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public MaxAlleleCountFilterFactory(final Fetcher<BaseCallCount> bccFetcher) {
		super(getOptionBuilder().build());
		maxAlleles = MAX_ALLELES;
		this.bccFetcher = bccFetcher;
	}

	@Override
	protected AbstractFilter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new MaxAlleleFilter(getC(), maxAlleles, bccFetcher);
	}
	
	@Override
	public RecordWrapperDataCache createFilterCache(
			AbstractConditionParameter conditionParameter,
			SharedCache sharedCache) {

		return null;
	}
	
	@Override
	public Options getOptions() {
		final Options options = new Options();
		options.addOption(getMaxAlleleOptionBuilder(MAX_ALLELES).build());
		return options;
	}
	
	@Override
	public void inidDataTypeContainer(AbstractBuilder builder) {
		// not needed
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) throws IllegalArgumentException {
		final Set<Option> parsed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "maxAlleles":
				final int maxAllelesOptionValue = Integer.valueOf(cmd.getOptionValue(longOpt));
				if (maxAllelesOptionValue < 1) {
					throw new IllegalArgumentException("Invalid allele count: " + longOpt);
				}
				maxAlleles = maxAllelesOptionValue;
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}
	
	/**
	 * TODO add comments.
	 */

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('M'))
				.desc("Max allowed alleles per site.");
	}

	public static Builder getMaxAllelesOptionBuilder() {
		return getMaxAlleleOptionBuilder(MAX_ALLELES);
	}

	public static Builder getMaxAlleleOptionBuilder(final int defaultValue) {
		return Option.builder()
				.argName("MAXALLELES")
				.hasArg()
				.longOpt("maxAlleles")
				.desc("must be > 0. Default: " + defaultValue);
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer filteredData) {
		sb.append(Util.EMPTY_FIELD);
	}

	public int getMaxAlleles() {
		return maxAlleles;
	}
	
}
