package lib.stat.estimation;

import java.util.List;

import lib.stat.nominal.NominalData;

public interface ConditionEstimate {

	String getID();

	double[] getAlpha(int iteration);

	double[] getAlpha();

	double getLogLikelihood();

	double getLogLikelihood(int iteration);

	boolean isNumericallyStable();

	void setNumericallyUnstable();

	int getMaxIterations();

	default boolean previousEstimate() {
		return getNextIteration() > 0 || ! isNumericallyStable();
	}
	
	int getNextIteration();

	NominalData getNominalData();

	void add(double[] alpha, double likelihood);

	void addBacktrack();
	List<Integer> getBacktracks();
	
	void addReset();
	List<Integer> getResets();
	
	void clear();

	String toString();

}