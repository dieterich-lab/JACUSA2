package jacusa.filter.factory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.has.HasBaseCallCount;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class MaxAlleleCountFilterFactory<T extends AbstractData & HasBaseCallCount> 
extends AbstractFilterFactory<T> {

	// default value for max alleles
	private static final int MAX_ALLELES = 2;
	// chosen value
	private int alleles;

	public MaxAlleleCountFilterFactory() {
		super(getOptionBuilder().build());
		alleles = MAX_ALLELES;
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();
		options.addOption(getMaxAlleleOptionBuilder(MAX_ALLELES).build());
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) throws IllegalArgumentException {
		// format: M:2
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "maxAlleles":
				final int alleleCount = Integer.valueOf(cmd.getOptionValue(longOpt));
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count: " + longOpt);
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * TODO add comments.
	 */
	private class MaxAlleleFilter extends AbstractFilter<T> {
		
		public MaxAlleleFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			return parallelData.getCombinedPooledData()
					.getBaseCallCount().getAlleles().size() > alleles;
		}

		@Override
		public int getOverhang() { 
			return 0;
		}

	}

	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('M'))
				.desc("Max allowed alleles per site.");
	}

	public static Builder getMaxAlleleOptionBuilder(final int defaultValue) {
		return Option.builder()
				.longOpt("maxAlleles")
				.desc("must be > 0. Default: " + defaultValue);
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		sb.append(Util.EMPTY_FIELD);
	}
	
}
