package lib.data.result;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.estimate.MinkaParameter;
import lib.io.InputOutput;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.sample.EstimationSample;
import lib.stat.sample.provider.DeletionCountSampleProvider;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.util.Info;
import lib.util.Util;

public class InsertionCountResult implements Result {

	public static final String INSERTION_SCORE 	= "insertion_score";
	public static final String INSERTION_PVALUE = "insertion_pvalue";
	
	private static final long serialVersionUID = 1L;
	
	private final List<BaseSubstitution> baseSubs;
	private final Result result;

	private final EstimationSampleProvider estimationSampleProvider;
	private final EstimateDirMult dirMult;
	private final ChiSquaredDistribution dist;

	public InsertionCountResult(final SortedSet<BaseSubstitution> baseSubs, final Result result) {
		this.baseSubs 	= new ArrayList<>(baseSubs);
		this.result 	= result;
		
		final MinkaParameter minkaParameter = new MinkaParameter();
		this.estimationSampleProvider 		= new DeletionCountSampleProvider(minkaParameter.getMaxIterations());
		this.dirMult						= new EstimateDirMult(minkaParameter);
		this.dist							= new ChiSquaredDistribution(1);

		init();
	}

	@Override
	public boolean isFiltered() {
		return result.isFiltered();
	}
	
	@Override
	public void setFiltered(boolean isFiltered) {
		result.setFiltered(isFiltered);
	}
	
	@Override
	public Info getFilterInfo() {
		return result.getFilterInfo();
	}
	
	@Override
	public Info getFilterInfo(int valueIndex) {
		return result.getFilterInfo(valueIndex);
	}
	
	@Override
	public ParallelData getParellelData() {
		return result.getParellelData();
	}
	
	@Override
	public Info getResultInfo() {
		return result.getResultInfo();
	}
	
	@Override
	public Info getResultInfo(int valueIndex) {
		return result.getResultInfo(valueIndex);
	}
	
	@Override
	public double getStat() {
		return result.getStat();
	}
	
	@Override
	public double getStat(int valueIndex) {
		return result.getStat(valueIndex);
	}
	
	@Override
	public SortedSet<Integer> getValuesIndex() {
		return result.getValuesIndex();
	}
	
	@Override
	public int getValueSize() {
		return result.getValueSize();
	}
	
	private void init() {
		final ParallelData parallelData = getParellelData();
		for (final int valueIndex : result.getValuesIndex()) {
			boolean check = false;
			for (int condition = 0; condition < parallelData.getConditions(); ++condition) {
				final int replicates = parallelData.getReplicates(condition);
				for (int replicate = 0; replicate < replicates; ++replicate) {
					if (valueIndex == Result.TOTAL) {
						check |= addTotalInsertionCount(valueIndex, condition, replicate);
					} else {
						check |= addStratifiedInsertionCount(valueIndex, condition, replicate);
					}
				}
			}
			if (! check) {
				// cleanup
				for (int condition = 0; condition < parallelData.getConditions(); ++condition) {
					final int replicates = parallelData.getReplicates(condition);
					for (int replicate = 0; replicate < replicates; ++replicate) {
						final String key 		= getKey(condition, replicate);
						final Info resultInfo 	= result.getResultInfo(valueIndex);
						resultInfo.remove(key);
					}
				}
			} else {
				if (valueIndex == Result.TOTAL) {
					final EstimationSample[] estimationSamples = estimationSampleProvider.convert(parallelData);
					final double lrt 	= dirMult.getLRT(estimationSamples);
					final double pvalue = getPValue(lrt);
					result.getResultInfo(valueIndex).add(INSERTION_PVALUE, Util.format(pvalue));
					result.getResultInfo(valueIndex).add(INSERTION_SCORE, Util.format(lrt));
				}
			}
		}
	}

	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	private String getKey(final int condition, final int replicate) {
		return new StringBuilder()
				.append(InputOutput.INSERTION_FIELD)
				.append(condition + 1)
				.append(replicate + 1)
				.toString();
	}
	
	private String getValue(final int insertionCount, final int coverage) {
		return new StringBuilder()
				.append(insertionCount)
				.append(InputOutput.VALUE_SEP)
				.append(coverage)
				.toString();
	}
	
	private boolean addTotalInsertionCount(final int valueIndex, final int condition, final int replicate) {
		final DataContainer container = 
				result.getParellelData().getDataContainer(condition, replicate);
		final int insertionCount = container.getInsertionCount().getValue();
		final int coverage		= container.getCoverage().getValue();
		addInsertionCount(valueIndex, condition, replicate, insertionCount, coverage);
		return insertionCount > 0;
	}
	
	// stratified by base substitutions
	private boolean addStratifiedInsertionCount(final int valueIndex, final int condition, final int replicate) {
		final BaseSubstitution baseSub 	= baseSubs.get(valueIndex);
		final DataContainer container 	= result.getParellelData().getDataContainer(condition, replicate);
		final int insertionCount 		= container.getBaseSubstitution2InsertionCount().get(baseSub).getValue();
		final int coverage				= container.getBaseSubstitution2Coverage().get(baseSub).getValue();
		addInsertionCount(valueIndex, condition, replicate, insertionCount, coverage);
		return insertionCount > 0;
	}
	
	private void addInsertionCount(
			final int valueIndex, 
			final int condition, final int replicate, 
			final int insertionCount, final int coverage) {
	
		final String key 		= getKey(condition, replicate);
		final String value 		= getValue(insertionCount, coverage); 
		final Info resultInfo 	= result.getResultInfo(valueIndex);
		resultInfo.add(key, value);
	}
}
