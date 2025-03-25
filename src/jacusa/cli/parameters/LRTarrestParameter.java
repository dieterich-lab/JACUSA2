package jacusa.cli.parameters;

import jacusa.io.format.lrtarrest.BED6lrtArrestResultFormat;
import jacusa.method.lrtarrest.LRTarrestMethod;
import lib.cli.parameter.GeneralParameter;
import lib.stat.betabin.LRTarrestStatFactory;

/**
 * Class defines parameters and default values that are need for Linked Reverse 
 * Transcription arrest (lrt-arrest).
 */
public class LRTarrestParameter extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;

	public LRTarrestParameter(final int conditions) {
		super(conditions);
		// change defaults
		
		// reduce window size to save memory consumption
		// storing linkage between rt sites and mismatches is memory intensive
		setActiveWindowSize(5000);
		
		// test-statistic related
		setStatParameter(new StatParameter(
				new LRTarrestStatFactory(new GeneralParameter(0)), Double.NaN));
		// default output format
		setResultFormat(new BED6lrtArrestResultFormat(
				LRTarrestMethod.Factory.NAME, this));
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
