package lib.stat.estimation;

import java.util.ArrayList;
import java.util.List;

import lib.stat.nominal.NominalData;
import lib.util.Util;

public class DefaultConditionEstimate implements ConditionEstimate {

	private final String id;
	private final NominalData nominalData;
	
	private double[][] alpha;
	private double[] logLikelihood;
	private boolean numericallyStable;
	
	private final List<Integer> backtracks;
	private final List<Integer> resets;
	
	private boolean successfull;
	private boolean failed;
	
	private int iteration;

	public DefaultConditionEstimate(
			final String id, 
			final NominalData dirMultData, 
			final int maxIterations) {
		
		this.id				= id;
		this.nominalData	= dirMultData;

		alpha 				= new double[maxIterations][];
		logLikelihood 		= new double[maxIterations];
		numericallyStable 	= true;
		
		backtracks 			= new ArrayList<Integer>();
		resets 				= new ArrayList<Integer>();

		successfull			= false;
		failed				= false;
		
		iteration 			= 0;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public double[] getAlpha(final int iteration) {
		return alpha[iteration];
	}

	@Override
	public double[] getAlpha() {
		return alpha[iteration - 1];
	}
	
	@Override
	public boolean failed() {
		return failed;
	}
	
	@Override
	public boolean successfull() {
		return successfull;
	}
	
	public void setFailed() {
		failed = true;
		successfull = false;
	}
	
	public void setSuccessfull() {
		failed = false;
		successfull = true;
	}
	
	@Override
	public double getLogLikelihood() {
		return logLikelihood[iteration - 1];
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
		return logLikelihood.length;
	}
	
	@Override
	public int getNextIteration() {
		return iteration;
	}

	@Override
	public NominalData getNominalData() {
		return nominalData;
	}
	
	@Override
	public void addBacktrack() {
		backtracks.add(iteration);
	}
	
	@Override
	public void addReset() {
		resets.add(iteration);
	}
	
	@Override
	public void add(final double[] alpha, final double likelihood) {
		this.alpha[iteration] 			= alpha;
		this.logLikelihood[iteration] 	= likelihood;
		iteration++;
	}
	
	@Override
	public void clear() {
		numericallyStable 	= true;
		iteration 			= 0;
		
		backtracks.clear();
		resets.clear();
		successfull 		= false;
		failed 				= false;
	}
	
	public List<Integer> getBacktracks() {
		return backtracks;
	}
	
	public List<Integer> getResets() {
		return resets;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Iteration: ");
		sb.append(iteration);
		sb.append('\n');
		
		sb.append("Initial Alpha: ");
		sb.append(Util.join(getAlpha(0), '\n'));
		sb.append('\n');
		
		for (int i = 0; i < iteration ; ++iteration) {
			sb.append("Iteartion: ");
			sb.append(iteration);
			sb.append(' ');
			sb.append(Util.join(getAlpha(iteration), '\n'));
		}
		
		sb.append("Final Alpha: ");
		sb.append(Util.join(getAlpha(), '\n'));
		sb.append('\n');
		
		sb.append("logLikelihood: ");
		sb.append(getLogLikelihood());
		sb.append('\n');

		return sb.toString();
	}
	
}
