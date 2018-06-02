package jacusa.filter.factory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.io.format.BEDlikeWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.has.HasBaseCallCount;
import lib.util.coordinate.CoordinateController;

/**
 * 
 *
 * @param <T>
 */
public class HomozygousFilterFactory<T extends AbstractData & HasBaseCallCount> 
extends AbstractFilterFactory<T> {

	// which condition is required to be homozygous
	private int homozygousConditionIndex;
	private final AbstractParameter<T, ?> parameters;

	public HomozygousFilterFactory(final AbstractParameter<T, ?> parameters) {
		super('H', 
				"Filter non-homozygous pileup/BAM in condition 1 or 2 " +
				"(MUST be set to H:1 or H:2). Default: none");
		homozygousConditionIndex 	= -1;
		this.parameters 			= parameters;
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();
		options.addOption(Option.builder("condition")
				.desc("Default: none")
				.build());
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) throws IllegalArgumentException {
		// format of s: 	H:<condition>[:strict]
		// array content:	0:1			  :2
		for (final Option option : cmd.getOptions()) {
			final String opt = option.getOpt();
			switch (opt) {
			case "condition":
				final int conditionIndex = Integer.parseInt(cmd.getOptionValue(opt));
				// make sure conditionIndex is within provided conditions
				if (conditionIndex >= 1 && conditionIndex <= parameters.getConditionsSize()) {
					this.homozygousConditionIndex = conditionIndex;
				} else {
					throw new IllegalArgumentException("Invalid argument: " + opt);
				}
				break;
				
			default:
				break;
			}
		}
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		// no caches are need
		// parallelData suffices to filter
		conditionContainer.getFilterContainer().addFilter(new HomozygousFilter(getC()));
	}
	
	/**
	 * TODO add comments. 
	 */
	private class HomozygousFilter 
	extends AbstractFilter<T> {

		public HomozygousFilter(final char c) {
			super(c);
		}

		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			final int alleles = parallelData.getPooledData(homozygousConditionIndex - 1)
					.getBaseCallCount().getAlleles().size();

			return alleles > 1;
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
