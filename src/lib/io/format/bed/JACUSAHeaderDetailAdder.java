package lib.io.format.bed;

import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.io.InputOutput;
import lib.util.AbstractTool;

public class JACUSAHeaderDetailAdder implements HeaderDetailAdder {

	@Override
	public void add(StringBuilder sb, List<ConditionParameter> conditionParameters) {
		sb.append(InputOutput.HEADER);
		sb.append(' ');
		sb.append(AbstractTool.getLogger().getTool().getCall());
		sb.append('\n');
	}

}
