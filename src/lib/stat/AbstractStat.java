package lib.stat;

import lib.data.ParallelData;
import lib.data.result.Result;

/**
 * 
 * @author Michael Piechotta
 */
public abstract class AbstractStat {

	private final AbstractStatFactory factory;

	public AbstractStat(final AbstractStatFactory factory) {
		this.factory = factory;
	}

	protected final AbstractStatFactory getFactory() {
		return factory;
	}

	protected abstract boolean filter(Result statResult);
	public abstract Result calculate(ParallelData parallelData);
	protected abstract void addStatResultInfo(Result statResult);
	
	public Result filter(final ParallelData parallelData) {
		final Result statResult = calculate(parallelData);
		if (filter(statResult)) {
			return null;
		}
		addStatResultInfo(statResult);
		return statResult;
	}
	
	
}
