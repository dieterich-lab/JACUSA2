package lib.io.variant;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.io.AbstractWriter;
import lib.variant.Variant;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractVariantWriter 
extends AbstractWriter {

	public AbstractVariantWriter(final String filename, final AbstractVariantFormat format) {
		super(filename, format);
	}

	public abstract void addVariants(final Variant[] variants, 
			final List<AbstractConditionParameter<?>> conditionParameters) throws Exception;

	public abstract AbstractVariantFormat getFormat();
	
	public abstract void addLine(final String line);
	
}
