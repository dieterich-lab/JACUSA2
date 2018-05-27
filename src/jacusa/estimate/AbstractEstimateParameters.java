package jacusa.estimate;

import lib.cli.options.Base;
import lib.data.count.PileupCount;
import lib.phred2prob.Phred2Prob;

/**
 * TODO comments
 */
public abstract class AbstractEstimateParameters {

	private final String name;
	private final String desc;
	protected final Phred2Prob phred2Prob;

	public AbstractEstimateParameters(final String name, final String desc, final Phred2Prob phred2Prob) {
		this.name = name;
		this.desc = desc;
		this.phred2Prob = phred2Prob;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public Phred2Prob getPhred2Prob() {
		return phred2Prob;
	}

	public abstract double[] estimateAlpha(Base[] bases, PileupCount[] pileupCounts);
	public abstract double[][] probabilityMatrix(Base[] bases, PileupCount[] pileupCounts);

}