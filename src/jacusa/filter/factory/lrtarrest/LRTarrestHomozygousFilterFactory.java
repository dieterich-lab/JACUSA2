package jacusa.filter.factory.lrtarrest;

import htsjdk.samtools.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.io.format.BEDlikeWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.count.BaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.result.Result;
import lib.util.coordinate.CoordinateController;

/**
 * 
 *
 * @param <T>
 */
public class LRTarrestHomozygousFilterFactory<T extends AbstractData & HasLRTarrestCount>
extends AbstractFilterFactory<T> {
	
	// which condition is required to be homozygous
	private int homozygousConditionIndex;
	private final AbstractParameter<T, ?> parameters;
	
	
	public LRTarrestHomozygousFilterFactory(final AbstractParameter<T, ?> parameters) {
		super(Option.builder(Character.toString('H'))
				.desc("Filter non-homozygous sites in condition 1 or 2 " +
						"(MUST be set to H:1 or H:2). Default: none")
				.build());
		homozygousConditionIndex 	= -1;
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

			default:
				throw new IllegalArgumentException("Invalid argument: " + opt);
			}
		}
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		// no caches are need
		// parallelData suffices to filter
		conditionContainer.getFilterContainer().addFilter(new HomozygousFilter(getC()));
	}
	
	@Override
	protected Options getOptions() {
		final Options options = new Options();
		options.addOption(HomozygousFilterFactory.getOptionBuilder().build());
		// TODO do we need this?
		// options.addOption(RTArrestFactory.getReadsOptionBuilder(appl).build());
		return options;
	}
	
	/**
	 * TODO add comments. 
	 */
	private class HomozygousFilter 
	extends AbstractFilter<T> {

		private final Set<Integer> filteredRefPositions;
		
		public HomozygousFilter(final char c) {
			super(c);
			filteredRefPositions 		= new HashSet<Integer>(5);
		}

		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			final T data = parallelData.getCombinedPooledData();

			if (data.getLRTarrestCount().getRefPos2bc4arrest().getRefPos().size() > 0) {
				filteredRefPositions.clear();
			}
			boolean filter = false;
			for (final int refPos : data.getLRTarrestCount().getRefPos2bc4arrest().getRefPos()) {
				final BaseCallCount baseCallCount = parallelData.getPooledData(homozygousConditionIndex - 1)
						.getLRTarrestCount().getRefPos2bc4arrest().getBaseCallCount(refPos);
				if (baseCallCount != null &&
						baseCallCount.getAlleles().size() > 1) {

					filter = true;
					filteredRefPositions.add(refPos);
				}
			}
			return filter;
		}

		@Override
		public void addInfo(Result<T> result) {
			final String value = StringUtil.join(Character.toString(BEDlikeWriter.VALUE_SEP), filteredRefPositions);
			// add position of artefact(s) to unique char id
			result.getFilterInfo().add(Character.toString(getC()), value);
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
