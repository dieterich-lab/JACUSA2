package jacusa.cli.parameters;

import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.builder.AbstractDataBuilderFactory;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class PileupParameters<T extends BaseQualData>
extends AbstractParameters<T> {

	public PileupParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditions, dataBuilderFactory);
	}

}
