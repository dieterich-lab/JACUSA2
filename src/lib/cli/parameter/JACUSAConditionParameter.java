package lib.cli.parameter;

import lib.data.AbstractData;
import lib.data.has.hasLibraryType;

public class JACUSAConditionParameter<T extends AbstractData>
extends AbstractConditionParameter<T>
implements hasLibraryType {
	
	public JACUSAConditionParameter(final int conditionIndex) {
		super(conditionIndex);
	}

}
