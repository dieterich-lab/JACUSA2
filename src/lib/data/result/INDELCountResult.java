package lib.data.result;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.data.ParallelData;
import lib.estimate.MinkaParameter;
import lib.io.InputOutput;
import lib.stat.dirmult.EstimateDirMult;
import lib.stat.estimation.EstimationContainer;
import lib.stat.estimation.provider.INDELestimationCountProvider;
import lib.util.Info;
import lib.util.Util;

/**
 * TODO
 */
abstract class INDELCountResult implements Result {
	
	private static final long serialVersionUID = 1L;
	
	private final List<BaseSub> baseSubs;
	private final Result result;

	private final INDELestimationCountProvider estContainerProv;
	private final EstimateDirMult dirMult;
	private final ChiSquaredDistribution dist;
	
	INDELCountResult(
			final SortedSet<BaseSub> baseSubs, final Result result,
			final MinkaParameter minkaParameter,
			final INDELestimationCountProvider countSampleProvider) {
		this.baseSubs 	= new ArrayList<>(baseSubs);
		this.result 	= result;
		
		this.estContainerProv 		= countSampleProvider;
		this.dirMult					= new EstimateDirMult(minkaParameter);
		this.dist						= new ChiSquaredDistribution(1);
		
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
						check |= addTotalCount(valueIndex, condition, replicate);
					} else {
						check |= addStratifiedCount(valueIndex, condition, replicate);
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
					final EstimationContainer[] estContainers = estContainerProv.convert(parallelData);
					final double lrt 	= dirMult.getLRT(estContainers);
					final double pvalue = getPValue(lrt);
					addPValue(result, valueIndex, Util.format(pvalue));
					addScore(result, valueIndex, Util.format(lrt));
				}
			}
		}
	}
	
	abstract void addPValue(Result result, int valueIndex,  String value);
	abstract void addScore(Result result, int valueIndex, String value);
	
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	
	private String getKey(final int condition, final int replicate) {
		return new StringBuilder()
				.append(InputOutput.DELETION_FIELD)
				.append(condition + 1)
				.append(replicate + 1)
				.toString();
	}
	
	private String getValue(final int count, final int coverage) {
		return new StringBuilder()
				.append(count)
				.append(InputOutput.VALUE_SEP)
				.append(coverage)
				.toString();
	}
	
	private boolean addTotalCount(final int valueIndex, final int condition, final int replicate) {
		final DataContainer container = 
				result.getParellelData().getDataContainer(condition, replicate);
		final int count 	= container.getDeletionCount().getValue();
		final int coverage	= getCount(container).getValue();
		addCount(valueIndex, condition, replicate, count, coverage);
		return count > 0;
	}
	
	abstract IntegerData getCount(DataContainer container);
	
	// stratified by base substitutions
	private boolean addStratifiedCount(final int valueIndex, final int condition, final int replicate) {
		final BaseSub baseSub 			= baseSubs.get(valueIndex);
		final DataContainer container 	= result.getParellelData().getDataContainer(condition, replicate);
		final int count 				= container.getBaseSub2DeletionCount().get(baseSub).getValue();
		final int coverage				= container.getBaseSub2Coverage().get(baseSub).getValue();
		addCount(valueIndex, condition, replicate, count, coverage);
		return count > 0;
	}
	
	private void addCount(
			final int valueIndex, 
			final int condition, final int replicate, 
			final int count, final int coverage) {
	
		final String key 		= getKey(condition, replicate);
		final String value 		= getValue(count, coverage); 
		final Info resultInfo 	= result.getResultInfo(valueIndex);
		resultInfo.add(key, value);
	}

}
