package lib.stat.dirmult;

public interface DirMultSample {

	String getId();

	double[] getAlpha(int iteration);
	double[] getAlpha();

	double getLogLikelihood();
	double getLogLikelihood(int iteration);

	boolean isNumericallyStable();
	void setNumericallyUnstable();

	int getMaxIterations();
	int getIteration();

	DirMultData getDirMultData();

	void add(double[] alpha, double likelihood);

	String toString();

	void clear();
	void clear(String id, DirMultData dirMultData);
	
}