package lib.io.format.bed;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.util.AbstractTool;
import lib.util.Util;

public class JACUSAHeaderDetailAdder implements HeaderDetailAdder {

	@Override
	public void add(StringBuilder sb, List<AbstractConditionParameter> conditionParameters) {
		sb.append(Util.HEADER);
		sb.append(' ');
		sb.append(AbstractTool.getLogger().getTool().getCall());
		sb.append('\n');
	}

}
