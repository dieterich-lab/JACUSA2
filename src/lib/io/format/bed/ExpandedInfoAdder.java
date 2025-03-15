package lib.io.format.bed;

import java.util.Set;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.io.InputOutput;

public class ExpandedInfoAdder implements InfoAdder {
	
	private final GeneralParameter parameter;
	private Set<String> additionalHeader;
	
	public ExpandedInfoAdder(final GeneralParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public void addHeader(StringBuilder sb) {
		// TODO
		
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		sb.append("info");
				
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		sb.append("filter");

		// always show reference base
		sb.append(InputOutput.FIELD_SEP);
		sb.append("ref");
		
		// TODO add registered column names
		for (final String s : additionalHeader) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(s);
		}
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		final ParallelData parallelData = result.getParellelData();

		sb.append(InputOutput.FIELD_SEP);
		// result.getResultInfo(valueIndex);
		// sb.append(value); // TODO add stuff that is not registered
		// result.getFilterInfo(valueIndex).combine()
		
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(result.getFilterInfo(valueIndex).combine());
		}

		// always show reference
		sb.append(InputOutput.FIELD_SEP);
		sb.append(parallelData.getCombPooledData().getAutoRefBase());
		
		// add result info
		/* FIXME
		 * for (final String value : result.getResultInfo(valueIndex).getValues()) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(value);
		}*/
	}

	protected void setup() {
		
	}
	
	public void getAdditionalHeader() {
		// TODO parse info parameters and create set of column names
	}
	
}
