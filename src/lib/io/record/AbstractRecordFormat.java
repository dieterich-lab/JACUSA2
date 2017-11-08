package lib.io.record;

import lib.cli.parameters.AbstractConditionParameter;
import lib.io.AbstractFormat;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractRecordFormat 
extends AbstractFormat {

	public AbstractRecordFormat(char c, String desc) {
		super(c, desc);
	}

	public abstract AbstractRecordWriter createWriterInstance(final AbstractConditionParameter<?> conditionParameter);

}
