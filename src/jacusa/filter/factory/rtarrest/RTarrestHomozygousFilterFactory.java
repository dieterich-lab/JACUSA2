package jacusa.filter.factory.rtarrest;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.format.BEDlikeWriter;
import jacusa.method.rtarrest.RTArrestFactory;
import jacusa.method.rtarrest.RTArrestFactory.RT_READS;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.count.BaseCallCount;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasThroughBaseCallCount;
import lib.util.coordinate.CoordinateController;

/**
 * 
 *
 * @param <T>
 */
public class RTarrestHomozygousFilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount> 
extends AbstractFilterFactory<T> {

	// which condition is required to be homozygous
	private int homozygousConditionIndex;
	private final Set<RT_READS> apply2reads;
	private final AbstractParameter<T, ?> parameters;

	public RTarrestHomozygousFilterFactory(final AbstractParameter<T, ?> parameters) {
		super('H', 
				"Filter non-homozygous pileup/BAM in condition 1 or 2.\n" +
				"Apply filter to read arrest OR read through reads OR both.\n" +
				"(H:[1|2]:[arrest|through|arrest&through]). Default: H[1|2]:arrest");
		homozygousConditionIndex 	= -1;
		apply2reads 				= new HashSet<RT_READS>(2);
		apply2reads.add(RT_READS.ARREST);
		this.parameters 			= parameters;
	}

	@Override
	public void processCLI(final CommandLine cmd) throws IllegalArgumentException {
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

			case "reads": // choose arrest, through or arrest&through
				final String optionValue = cmd.getOptionValue(opt);
				apply2reads.clear();
				apply2reads.addAll(RTArrestFactory.processApply2Reads(optionValue));
				break;

			default:
				throw new IllegalArgumentException("Invalid argument: " + opt);
			}
		}
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();
		options.addOption(RTArrestFactory.getOption());
		return options;
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
			BaseCallCount baseCallCount = null;
			if (apply2reads.size() == 2) {
				baseCallCount = parallelData.getPooledData(homozygousConditionIndex - 1).getBaseCallCount();
			} else {
				if (apply2reads.contains(RT_READS.ARREST)) {
					baseCallCount = parallelData.getPooledData(homozygousConditionIndex - 1).getArrestBaseCallCount();	
				} else if (apply2reads.contains(RT_READS.THROUGH)) {
					baseCallCount = parallelData.getPooledData(homozygousConditionIndex - 1).getThroughBaseCallCount();	
				}
			}

			return baseCallCount.getAlleles().size() > 1;
		}
		
		@Override
		public int getOverhang() { 
			return 0; 
		}

	}

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		sb.append(BEDlikeWriter.EMPTY_FIELD);	
	}
	
}
