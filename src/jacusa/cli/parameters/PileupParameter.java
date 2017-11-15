package jacusa.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.DefaultResult;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class PileupParameter<T extends AbstractData & hasPileupCount>
extends AbstractParameter<T, DefaultResult<T>> {

	public PileupParameter(final int conditions) {
		super(conditions);
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter() {
		return new JACUSAConditionParameter<T>();
	}
	
}
