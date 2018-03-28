package lib.cli.parameter;

import lib.data.AbstractData;
import lib.data.has.HasLibraryType;

public class JACUSAConditionParameter<T extends AbstractData>
extends AbstractConditionParameter<T>
implements HasLibraryType {
	
	public JACUSAConditionParameter(final int conditionIndex) {
		super(conditionIndex);
	}

}
