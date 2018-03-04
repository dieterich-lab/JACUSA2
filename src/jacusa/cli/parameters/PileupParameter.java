package jacusa.cli.parameters;

import jacusa.io.writer.BED6pileupResultFormat;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.PileupData;
import lib.data.result.DefaultResult;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class PileupParameter
extends AbstractParameter<PileupData, DefaultResult<PileupData>> {

	public PileupParameter(final int conditions) {
		super(conditions);

		// set pileup method specific result format
		setResultFormat(new BED6pileupResultFormat<PileupData, DefaultResult<PileupData>>(this));
	}

	@Override
	public void setDefaultValues() {
		setResultFormat(new BED6pileupResultFormat<PileupData, DefaultResult<PileupData>>(this));	
	}
	
	@Override
	public AbstractConditionParameter<PileupData> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<PileupData>(conditionIndex);
	}
	
}
