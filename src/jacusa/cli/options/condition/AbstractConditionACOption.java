package jacusa.cli.options.condition;

import java.util.ArrayList;
import java.util.List;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

public abstract class AbstractConditionACOption<T extends AbstractData> extends AbstractACOption {

	private int conditionIndex;
	private List<ConditionParameters<T>> conditions;
		
	public AbstractConditionACOption(final String opt, final String longOpt, List<ConditionParameters<T>> conditions) {
		super(opt, longOpt);
		conditionIndex 	= -1;
		this.conditions = conditions;
	}
	
	public AbstractConditionACOption(final String opt, final String longOpt, final int conditionIndex, final ConditionParameters<T> condition) {
		super(! opt.isEmpty() ? opt + conditionIndex: new String(),
				! longOpt.isEmpty() ? longOpt + conditionIndex : new String());

		this.conditionIndex = conditionIndex;
		conditions = new ArrayList<ConditionParameters<T>>(1);
		conditions.add(condition);
	}
	
	public List<ConditionParameters<T>> getConditions() {
		return conditions;
	}
	
	public int getConditionIndex() {
		return conditionIndex;
	}

}
