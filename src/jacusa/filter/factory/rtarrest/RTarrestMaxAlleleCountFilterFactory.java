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
 * @author Michael Piechotta
 *
 */
public class RTarrestMaxAlleleCountFilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount> 
extends AbstractFilterFactory<T> {

	// default value for max alleles
	private static final int MAX_ALLELES = 2;
	// chosen value
	private int alleles;
	private final Set<RT_READS> apply2reads;

	public RTarrestMaxAlleleCountFilterFactory() {
		super('M', 
				"Max allowed alleles per parallel pileup.\n" +
				"Apply filter to read arrest OR read through reads OR both.\n" +
				"(M:<MAX_ALLELS>:[arrest|through|arrest&through])" +
				"Default: "+ MAX_ALLELES + ":arrest");
		alleles 	= MAX_ALLELES;
		apply2reads = new HashSet<RT_READS>(2);
		apply2reads.add(RT_READS.ARREST);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
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
		options.addOption(Option.builder("maxAlleles")
				.desc("Default: " + MAX_ALLELES)
				.build());
		options.addOption(RTArrestFactory.getOption());
		return options;
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
			BaseCallCount baseCallCount = null;
			if (apply2reads.size() == 2) {
				baseCallCount = parallelData.getCombinedPooledData().getBaseCallCount();
			} else {
				if (apply2reads.contains(RT_READS.ARREST)) {
					baseCallCount = parallelData.getCombinedPooledData().getArrestBaseCallCount();	
				} else if (apply2reads.contains(RT_READS.THROUGH)) {
					baseCallCount = parallelData.getCombinedPooledData().getThroughBaseCallCount();	
				}
			}

			return baseCallCount.getAlleles().size() > alleles;
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
