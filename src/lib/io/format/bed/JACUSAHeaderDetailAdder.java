package lib.io.format.bed;

import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.util.AbstractTool;
import lib.util.Util;

public class JACUSAHeaderDetailAdder implements HeaderDetailAdder {

	@Override
	public void add(StringBuilder sb, List<ConditionParameter> conditionParameters) {
		sb.append(Util.HEADER);
		sb.append(' ');
		sb.append(AbstractTool.getLogger().getTool().getCall());
		sb.append('\n');
	}

}
