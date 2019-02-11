package lib.stat.sample;

import lib.stat.nominal.NominalData;
import lib.util.Util;

public class DefaultEstimationSample implements EstimationSample {

	private String id;
	private NominalData nominalData;
	private final int maxIterations;
	
	private double[][] alpha;
	private double[] logLikelihood;
	private boolean numericallyStable;
	
	private int iteration;

	public DefaultEstimationSample(final String id, final NominalData dirMultData, final int maxIterations) {
		this.id				= id;
		this.nominalData	= dirMultData;
		this.maxIterations 	= maxIterations;

		alpha 				= new double[maxIterations + 1][];
		logLikelihood 		= new double[maxIterations + 1];
		numericallyStable 	= true;
		
		iteration 			= -1;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public double[] getAlpha(final int iteration) {
		return alpha[iteration];
	}

	@Override
	public double[] getAlpha() {
		return alpha[getIteration()];
	}
	
	@Override
	public double getLogLikelihood() {
		return logLikelihood[getIteration()];
	}
	
	@Override
	public double getLogLikelihood(final int iteration) {
		return logLikelihood[iteration];
	}
	
	@Override
	public boolean isNumericallyStable() {
		return numericallyStable;
	}
	
	@Override
	public void setNumericallyUnstable() {
		this.numericallyStable = false;
	}

	@Override
	public int getMaxIterations() {
		return maxIterations;
	}

	@Override
	public int getIteration() {
		return iteration;
	}

	@Override
	public NominalData getNominalData() {
		return nominalData;
	}
	
	@Override
	public void add(final double[] alpha, final double likelihood) {
		iteration++;
		this.alpha[iteration] 			= alpha;
		this.logLikelihood[iteration] 	= likelihood;
	}
	
	@Override
	public void update(final String id, final NominalData nominalData) {
		this.id				= id;
		this.nominalData	= nominalData;
	}

	@Override
	public void clear() {
		numericallyStable 	= true;
		iteration 			= -1;		
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Iteration: ");
		sb.append(iteration);
		sb.append('\n');
		
		sb.append("Initial Alpha: ");
		sb.append(Util.printAlpha(getAlpha(0)));
		sb.append('\n');
		
		sb.append("Final Alpha: ");
		sb.append(Util.printAlpha(getAlpha()));
		sb.append('\n');
		
		sb.append("logLikelihood: ");
		sb.append(getLogLikelihood());
		sb.append('\n');

		return sb.toString();
	}
	
}
