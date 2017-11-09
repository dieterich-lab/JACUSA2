package jacusa.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.builder.factory.AbstractDataBuilderFactory;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class PileupParameters<T extends PileupData>
extends AbstractParameter<T> {

	public PileupParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditions, dataBuilderFactory);
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter(
			AbstractDataBuilderFactory<T> dataBuilderFactory) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
