package lib.stat.estimation;

import lib.stat.nominal.NominalData;

/**
 * TODO add documentation
 */
public interface EstimationContainer {
	
	String getID();
	
	double[] getAlpha(int iteration);
	double[] getAlpha();
	
	double getLogLikelihood();
	double getLogLikelihood(int iteration);
	
	boolean isNumericallyStable();
	void setNumericallyUnstable();
	
	int getMaxIterations();
	int getIteration();
	
	NominalData getNominalData();
	
	void add(double[] alpha, double likelihood);
	
	String toString();
	
	void clear();
	void update(String id, NominalData nominalData);
	
}