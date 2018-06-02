package jacusa.filter.factory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.io.format.BEDlikeWriter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.has.HasBaseCallCount;
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
		super('M', 
				"Max allowed alleles per parallel pileup.");
		alleles = MAX_ALLELES;
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();
		options.addOption(Option.builder("maxAlleles")
				.desc("Default: " + MAX_ALLELES)
				.build());
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) throws IllegalArgumentException {
		// format: M:2
		for (final Option option : cmd.getOptions()) {
			final String opt = option.getOpt();
			switch (opt) {
			case "maxAlleles":
				final int alleleCount = Integer.valueOf(cmd.getOptionValue(opt));
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count: " + opt);
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

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		sb.append(BEDlikeWriter.EMPTY);
	}
	
}
