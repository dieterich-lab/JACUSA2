package lib.io.format.bed;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;

public interface HeaderDetailAdder {

	void add(StringBuilder sb, List<AbstractConditionParameter> conditionParameters);
	
}
