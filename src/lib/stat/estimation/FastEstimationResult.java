package lib.stat.estimation;

import lib.stat.nominal.NominalData;
import lib.util.Util;

public class FastEstimationResult implements EstimationContainer {

	private String id;
	private NominalData nominalData;
	private final int maxIterations;

	private double[] initAlpha;
	private double[] alpha;
	
	private double logLikelihood;
	private boolean numericallyStable;
	
	private int iteration;

	public FastEstimationResult(final String id, final NominalData nominalData, final int maxIterations) {
		this.id				= id;
		this.nominalData	= nominalData;
		this.maxIterations 	= maxIterations;

		logLikelihood 		= Double.NaN;
		numericallyStable 	= true;
		
		iteration 			= -1;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public double[] getAlpha(final int iteration) {
		if (iteration == 0) {
			return initAlpha;			
		} else if(this.iteration == iteration) {
			return alpha;
		} else {
			throw new IllegalArgumentException("Does not support random access to iteration");
		}
	}

	@Override
	public double[] getAlpha() {
		return getAlpha(iteration);
	}
	
	@Override
	public double getLogLikelihood() {
		return logLikelihood;
	}
	
	@Override
	public double getLogLikelihood(final int iteration) {
		if(this.iteration == iteration) {
			return logLikelihood;
		} else {
			throw new IllegalArgumentException("Does not support random access to iteration");
		}
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
	public void add(final double[] alpha, final double logLikelihood) {
		iteration++;
		if (iteration == 0) {
			this.initAlpha 	= alpha;
		} else {
			this.alpha 		= alpha;
			
		}
		this.logLikelihood 	= logLikelihood;
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
