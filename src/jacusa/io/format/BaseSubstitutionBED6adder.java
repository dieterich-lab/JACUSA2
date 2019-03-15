package jacusa.io.format;

import java.util.List;

import lib.cli.options.filter.has.HasReadSubstitution;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.BED6adder;

/**
 * This class corresponds to the a column of a BEDlike output file that contains read base substitution 
 * information, specifically which base substitution is being considered
 */
@Deprecated
public class BaseSubstitutionBED6adder implements BED6adder {

	// list of chosen base substitutions
	private final List<BaseSubstitution> baseSubs;
	// BED6adder that should be written before this object
	private final BED6adder bed6adder;
	
	public BaseSubstitutionBED6adder(
			final List<BaseSubstitution> baseSubs,
			final BED6adder bed6adder) {
		
		this.baseSubs 	= baseSubs;
		this.bed6adder 	= bed6adder;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		bed6adder.addHeader(sb);
		sb.append(InputOutput.FIELD_SEP);
		sb.append(HasReadSubstitution.READ_SUB);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		bed6adder.addData(sb, valueIndex, result);
		sb.append(InputOutput.FIELD_SEP);
		if (valueIndex == Result.TOTAL) { 
			sb.append(InputOutput.EMPTY_FIELD);
		} else if (valueIndex >= 0){
			sb.append(baseSubs.get(valueIndex).toString());
		}
	}	
}
