package jacusa.cli.parameters;

import jacusa.data.BaseQualData;
import jacusa.pileup.builder.AbstractDataBuilderFactory;

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
