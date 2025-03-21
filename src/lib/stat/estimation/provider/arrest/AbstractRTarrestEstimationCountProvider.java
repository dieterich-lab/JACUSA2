package lib.stat.estimation.provider.arrest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.stat.estimation.ConditionEstimate;
import lib.stat.estimation.DefaultConditionEstimate;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.ConditionEstimateProvider;
import lib.stat.nominal.NominalData;

public abstract class AbstractRTarrestEstimationCountProvider implements ConditionEstimateProvider {

	public static final int READ_ARREST_INDEX	= 0;
	public static final int READ_THROUGH_INDEX	= 1;

	public enum READ_INDEX {
		ARREST(0), THROUGH(1);

		private final int value;

		private READ_INDEX(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	private final int maxIterations;
	private final double pseudoCount;

	public AbstractRTarrestEstimationCountProvider(final int maxIterations, final double pseudoCount) {
		this.maxIterations 	= maxIterations;
		this.pseudoCount 	= 1d;
	}

	protected abstract List<List<Count>> process(ParallelData parallelData);

	@Override
	public EstimationContainer convert(ParallelData parallelData) {
		final List<List<Count>> counts = process(parallelData);

		final ConditionEstimate[] conditionEstimates = new ConditionEstimate[parallelData.getConditions()];
		
		for (int conditionIndex = 0; conditionIndex < conditionEstimates.length; ++conditionIndex) {
			final NominalData nominalData 		= createData(counts.get(conditionIndex)); 
			conditionEstimates[conditionIndex] 	= createContainer(
					Integer.toString(conditionIndex + 1),
					nominalData,
					maxIterations);
		}

		// conditions pooled
		final NominalData nominalData = createData(
				counts.stream()
					.flatMap(List::stream)
					.collect(Collectors.toList()));

		return new EstimationContainer(conditionEstimates, new DefaultConditionEstimate("P", nominalData, maxIterations));
	}

	private ConditionEstimate createContainer(final String id, final NominalData nominalData, final int maxIterations) {
		return new DefaultConditionEstimate(id, nominalData, maxIterations);
	}

	public NominalData createData(final List<Count> counts) {
		final int catergories = 2;
		final double[][] dataMatrix  = new double[counts.size()][catergories]; // -> 2 because BetaBin
		for (int replicateIndex = 0; replicateIndex < counts.size(); replicateIndex++) {
			final Count count = counts.get(replicateIndex);
			final int readArrestCount 	= count.arrest;
			final int readThroughCount 	= count.through;
			dataMatrix[replicateIndex][READ_ARREST_INDEX] 	= readArrestCount + pseudoCount;
			dataMatrix[replicateIndex][READ_THROUGH_INDEX] 	= readThroughCount + pseudoCount;
			
		}
		return NominalData.build(catergories, dataMatrix);
	}

	
	protected List<List<Count>> getCounts(final ParallelData parallelData) {
		final int conditions = parallelData.getConditions();
		final List<List<Count>> originalCounts = new ArrayList<>(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final List<Count> tmpDataContainers = new ArrayList<>(parallelData.getData(conditionIndex).size());
			for (final DataContainer container : parallelData.getData(conditionIndex)) {
				Count count = new Count(
						container.getArrestBaseCallCount().getCoverage(),
						container.getThroughBaseCallCount().getCoverage());
				tmpDataContainers.add(count);
			}
			originalCounts.add(tmpDataContainers);
		}
		return originalCounts;
	}

	protected List<Count> flat(final List<Count> counts,
			final READ_INDEX exclusiveIndex) {

		final List<Count> flatCounts = new ArrayList<>(counts.size());
		for (final Count count : counts) {
			final Count countCopy = count.copy();
			countCopy.flat(exclusiveIndex);
			flatCounts.add(countCopy);
		}
		return flatCounts;
	}

	public class Count {

		public int arrest;
		public int through;

		public Count(final int arrest, final int through) {
			this.arrest = arrest;
			this.through = through;
		}

		public Count(DataContainer container) {
			this.arrest = container.getArrestBaseCallCount().getCoverage();
			this.through = container.getThroughBaseCallCount().getCoverage();
		}

		public Count copy() {
			return new Count(arrest, through);
		}

		public void flat(final READ_INDEX readIndex) {
			if (readIndex == READ_INDEX.ARREST) { 
				through += arrest;
				arrest = 0;
			} else {
				arrest += through;
				through = 0;
			}
		}

		public boolean both() {
			return arrest > 0 && through > 0;
		}

	}

}
