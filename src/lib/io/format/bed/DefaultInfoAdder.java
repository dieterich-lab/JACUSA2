package lib.io.format.bed;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.io.InputOutput;

public class DefaultInfoAdder implements InfoAdder {
	
	private final GeneralParameter parameter;
	
	public DefaultInfoAdder(final GeneralParameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append("info");

		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		sb.append("filter");

		// always show reference base
		sb.append(InputOutput.FIELD_SEP);
		sb.append("ref");
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		final ParallelData parallelData = result.getParellelData();

		// add result info
		sb.append(InputOutput.FIELD_SEP);
		sb.append(result.getResultInfo(valueIndex).combine());
		
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(result.getFilterInfo(valueIndex).combine());
		} else {
			sb.append(InputOutput.EMPTY_FIELD);
		}

		// always show reference
		sb.append(InputOutput.FIELD_SEP);
		sb.append(parallelData.getCombPooledData().getAutoRefBase());
	}

}
