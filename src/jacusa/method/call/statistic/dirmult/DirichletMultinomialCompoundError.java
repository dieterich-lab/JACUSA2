package jacusa.method.call.statistic.dirmult;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.estimate.MinkaEstimateDirMultParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.AbstractDirichletStatistic;

public class DirichletMultinomialCompoundError<T extends BaseQualData>
extends AbstractDirichletStatistic<T> {

	protected double estimatedError = 0.01;
	protected double priorError = 0d;

	public DirichletMultinomialCompoundError(final CallParameters<T> parameters) {
		// sorry for ugly, code call to super constructor must be first call
		super(new MinkaEstimateDirMultParameters(), parameters);
	}

	@Override
	public String getName() {
		return "DirMult-CE";
	}

	@Override
	public String getDescription() {
		return "Compound Err. (estimated err.{" + estimatedError + "} + phred score)";  
	}

	@Override
	protected void populate(final T pileup, final int[] baseIndexs, double[] pileupMatrix) {
		double[] pileupCount = phred2Prob.colSumCount(baseIndexs, pileup);
		double[] pileupError = phred2Prob.colMeanErrorProb(baseIndexs, pileup);

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
	public DirichletMultinomialCompoundError<T> newInstance() {
		return new DirichletMultinomialCompoundError<T>(parameters);
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