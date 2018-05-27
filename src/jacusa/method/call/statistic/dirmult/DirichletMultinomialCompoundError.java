package jacusa.method.call.statistic.dirmult;

import jacusa.cli.parameters.CallParameter;
import jacusa.estimate.MinkaEstimateDirMultParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.AbstractDirichletStatistic;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;

public class DirichletMultinomialCompoundError<T extends AbstractData & HasPileupCount>
extends AbstractDirichletStatistic<T> {

	private static final String NAME = "DirMult-CE";
	private static final double ESTIMATED_ERROR = 0.01;
	private static final String DESC = "Compound Err. (estimated err.{" + ESTIMATED_ERROR + "} + phred score)";
	
	protected double estimatedError = ESTIMATED_ERROR;
	protected double priorError = 0d;

	protected DirichletMultinomialCompoundError(final String name, final String desc, final CallParameter parameter) {
		super(name, desc, new MinkaEstimateDirMultParameters(), parameter);
	}
	
	public DirichletMultinomialCompoundError(final CallParameter parameter) {
		super(NAME, DESC, new MinkaEstimateDirMultParameters(), parameter);
	}

	@Override
	protected void populate(final T data, final Base[] bases, double[] pileupMatrix) {
		double[] pileupCount = phred2Prob.colSumCount(bases, data);
		double[] pileupError = phred2Prob.colMeanErrorProb(bases, data.getPileupCount());

		for (final Base base : bases) {
			pileupMatrix[base.getIndex()] += priorError;

			if (pileupCount[base.getIndex()] > 0.0) {
				pileupMatrix[base.getIndex()] += pileupCount[base.getIndex()];
				for (final Base base2 : bases) {
					if (base != base2) {
						double combinedError = (pileupError[base2.getIndex()] + estimatedError) * (double)pileupCount[base.getIndex()] / (double)(bases.length - 1);
						pileupMatrix[base2.getIndex()] += combinedError;
					} else {
						// pileupMatrix[pileupI][baseI2] -= (estimatedError) * (double)pileupCount[baseI];
					}
				}
			} else {
			
			}
		}
	}

	@Override
	public AbstractDirichletStatistic<T> newInstance() {
		return new DirichletMultinomialCompoundError<T>(parameter);
	}
	
	@Override
	public boolean processCLI(String line) {
		boolean r = super.processCLI(line);
		String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));

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