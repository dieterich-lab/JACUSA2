package jacusa.io.format;

import java.util.List;

import lib.cli.options.has.HasReadSubstitution;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.result.Result;
import lib.io.format.bed.BED6adder;
import lib.util.Util;

public class BaseSubstitutionBED6adder implements BED6adder {

	private final List<BaseSubstitution> baseSubs; 
	private final BED6adder bed6adder;
	
	public BaseSubstitutionBED6adder(
			final List<BaseSubstitution> baseSubs,
			final BED6adder bed6adder) {
		
		this.baseSubs = baseSubs;
		this.bed6adder = bed6adder;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		bed6adder.addHeader(sb);
		sb.append(Util.FIELD_SEP);
		sb.append(HasReadSubstitution.READ_SUB);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		bed6adder.addData(sb, valueIndex, result);
		sb.append(Util.FIELD_SEP);
		sb.append(Util.FIELD_SEP);
		if (valueIndex == 0) {
			sb.append(Util.EMPTY_FIELD);
		} else {
			sb.append(baseSubs.get(valueIndex - 1).toString());
		}
	}	
}
