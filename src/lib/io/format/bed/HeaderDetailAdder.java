package lib.io.format.bed;

import java.util.List;

import lib.cli.parameter.ConditionParameter;

public interface HeaderDetailAdder {

	void add(StringBuilder sb, List<ConditionParameter> conditionParameters);
	
}
