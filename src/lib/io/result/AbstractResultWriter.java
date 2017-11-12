package lib.io.result;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.Result;
import lib.io.AbstractWriter;
import lib.io.variant.AbstractVariantFormat;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractResultWriter<T extends AbstractData> 
extends AbstractWriter {

	public AbstractResultWriter(final String filename, final AbstractResultFormat<T> format) {
		super(filename, format);
	}

	public abstract void addResult(final Result<T> result, 
			final List<AbstractConditionParameter<?>> conditionParameters) throws Exception;

	public abstract AbstractVariantFormat getFormat();
	
	public abstract void addLine(final String line);
	
}
