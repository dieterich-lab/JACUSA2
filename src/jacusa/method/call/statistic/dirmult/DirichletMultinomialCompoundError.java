package jacusa.method.call.statistic.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.cli.parameters.CallParameter;
import jacusa.estimate.MinkaEstimateDirMultParameters;
import jacusa.method.call.statistic.AbstractDirichletStatistic;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.util.Base;

public class DirichletMultinomialCompoundError<T extends AbstractData & HasPileupCount>
extends AbstractDirichletStatistic<T> {

	private static final String NAME = "DirMultCE";
	public static final double ESTIMATED_ERROR = 0.01;
	public static final String DESC = "Compound Error (estimated error {" + ESTIMATED_ERROR + "} + phred score)";
	
	protected double estimatedError = ESTIMATED_ERROR;
	protected double priorError = 0d;

	protected DirichletMultinomialCompoundError(final Option option, final CallParameter parameter) {
		super(option, new MinkaEstimateDirMultParameters(), parameter);
	}
	
	public DirichletMultinomialCompoundError(final CallParameter parameter) {
		super(Option.builder(NAME)
				.desc(DESC)
				.build(), 
				new MinkaEstimateDirMultParameters(), 
				parameter);
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
	protected Options getOptions() {
		final Options options = super.getOptions();
		options.addOption(Option.builder("estimatedError")
				.hasArg(true)
				.desc("")
				.build());
		return options; 
	}

	
	@Override
	public void processCLI(final CommandLine cmd) {
		// format: -u DirMult:epsilon=<epsilon>:maxIterations=<maxIterions>:onlyObserved
	
		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
				case "estimatedError":
				estimatedError = Double.parseDouble(cmd.getOptionValue(longOpt));
				break;
	
			default:
				break;
			}
		}
	}


	public void setEstimatedError(double estimatedError) {
		this.estimatedError = estimatedError;
	}
	
}