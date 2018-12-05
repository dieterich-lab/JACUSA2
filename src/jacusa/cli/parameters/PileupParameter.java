package jacusa.cli.parameters;

import jacusa.io.format.pileup.BED6pileupResultFormat;
import jacusa.method.pileup.PileupMethod;
import jacusa.method.rtarrest.CoverageStatisticFactory;
import lib.cli.parameter.GeneralParameter;

/**
 * Class defines parameters and default values that are need for pileup method.
 */
public class PileupParameter
extends GeneralParameter
implements HasStatParameter {

	private StatParameter statParameter;
	
	public PileupParameter(final int conditions) {
		super(conditions);
		setStatParameter(new StatParameter(
				new CoverageStatisticFactory(), Double.NaN));
		setResultFormat(
				new BED6pileupResultFormat(PileupMethod.Factory.NAME, this));
	}

	@Override
	public StatParameter getStatParameter() {
		return statParameter;
	}

	@Override
	public void setStatParameter(final StatParameter statParameter) {
		this.statParameter = statParameter;
		
	}

	
}
