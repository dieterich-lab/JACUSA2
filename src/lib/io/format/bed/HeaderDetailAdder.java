package lib.io.format.bed;

import java.util.List;

import lib.cli.parameter.ConditionParameter;

/**
 * TODO add documentation
 */
public interface HeaderDetailAdder {

	void add(StringBuilder sb, List<ConditionParameter> conditionParameters);
	
}
