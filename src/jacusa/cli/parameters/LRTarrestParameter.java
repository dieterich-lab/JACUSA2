package jacusa.cli.parameters;

import lib.cli.parameter.GeneralParameter;
import lib.stat.betabin.LRTarrestBetaBinParameter;

/**
 * Class defines parameters and default values that are need for Linked Reverse 
 * Transcription arrest (lrt-arrest).
 */
public class LRTarrestParameter extends GeneralParameter 
implements HasStatParameter {

	private StatParameter statParameter;

	private LRTarrestBetaBinParameter betaBinParameter;
	
	public LRTarrestParameter(final int conditions) {
		super(conditions);
		// change defaults
		
		// reduce window size to save memory consumption
		// storing linkage between rt sites and mismatches is memory intensive
		setActiveWindowSize(5000);
	}

	public LRTarrestBetaBinParameter getLRTarrestBetaBinParameter() {
		return betaBinParameter;
	}
	
	@Override
	public StatParameter getStatParameter() {
		return statParameter;
	}

	@Override
	public void setStatParameter(final StatParameter statParameter) {
		this.statParameter = statParameter;
	}
	
	/* TODO finish implementation
	@Override
	public void registerKeys() {
		registerKey(LRTarrestWorker.INFO_VARIANT_SCORE);
	}
	*/
	
}
