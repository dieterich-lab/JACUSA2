package jacusa.method.call.statistic.dirmult;

import jacusa.cli.parameters.CallParameter;
import jacusa.estimate.MinkaEstimateDirMultParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.AbstractDirichletStatistic;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;

public class DirichletMultinomialCompoundError<T extends AbstractData & hasPileupCount>
extends AbstractDirichletStatistic<T> {

	private static final String NAME = "DirMult-CE";
	private static final double ESTIMATED_ERROR = 0.01;
	private static final String DESC = "Compound Err. (estimated err.{" + ESTIMATED_ERROR + "} + phred score)";
	
	protected double estimatedError;
	protected double priorError = 0d;

	public DirichletMultinomialCompoundError(final CallParameter parameters) {
		this(Double.NaN, parameters);
	}

	public DirichletMultinomialCompoundError(final double threshold, final CallParameter parameter) {
		super(NAME, DESC, threshold, new MinkaEstimateDirMultParameters(), parameter);
	}

	@Override
	protected void populate(final T data, final int[] baseIndexs, double[] pileupMatrix) {
		double[] pileupCount = phred2Prob.colSumCount(baseIndexs, data);
		double[] pileupError = phred2Prob.colMeanErrorProb(baseIndexs, data.getPileupCount());

		for (int baseI : baseIndexs) {
			pileupMatrix[baseI] += priorError;

			if (pileupCount[baseI] > 0.0) {
				pileupMatrix[baseI] += pileupCount[baseI];
				for (int baseI2 : baseIndexs) {
					if (baseI != baseI2) {
						double combinedError = (pileupError[baseI2] + estimatedError) * (double)pileupCount[baseI] / (double)(baseIndexs.length - 1);
						pileupMatrix[baseI2] += combinedError;
					} else {
						// pileupMatrix[pileupI][baseI2] -= (estimatedError) * (double)pileupCount[baseI];
					}
				}
			} else {
			
			}
		}
	}

	@Override
	public DirichletMultinomialCompoundError<T> newInstance(final double threshold) {
		return new DirichletMultinomialCompoundError<T>(threshold, parameter);
	}
	
	@Override
	public boolean processCLI(String line) {
		boolean r = super.processCLI(line);
		String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

		for (int i = 1; i < s.length; ++i) {
			// key=value
			String[] kv = s[i].split("=");
			String key = kv[0];
			String value = new String();
			if (kv.length == 2) {
				value = kv[1];
			}

			// set value
			if (key.equals("estimatedError")) {
				estimatedError = Double.parseDouble(value);
				r = true;
			} else if (!r){
				throw new IllegalArgumentException("Invalid argument " + key + " in line: " + line);
			}
		}
		
		return r;
	}
	
	public void setEstimatedError(double estimatedError) {
		this.estimatedError = estimatedError;
	}
	
}